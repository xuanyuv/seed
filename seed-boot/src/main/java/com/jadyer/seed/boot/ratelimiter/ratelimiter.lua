--[[
  A lua rate limiter script run in redis use token bucket algorithm.
--]]

-- KEYS和ARGV相當於lua關鍵字，分別用來表示存儲在Redis中的key和redis命令傳給lua的參數，下標都是從1開始的
-- eval "return {KEYS[1],KEYS[2],ARGV[1],ARGV[2]}" 2 key1 key2 first second
-- eval是lua腳本解釋器，它的第一個參數是腳本內容，第二個參數是腳本裏面KEYS數組的長度（不包括ARGV參數的個數），第三個參數用於傳遞KEYS數組，後面剩下的參數會全部傳遞給ARGV數據
-- 上面說的是redis中調用lua，若在lua中調用redis可以使用redis.call()或redis.pcall()（二者的區別是遇到錯誤時返回的提示方式不同）

-- intervalPerPermit, time interval in millis between two token permits;
-- refillTime, timestamp when running this lua script;
-- limit, the capacity limit of the token bucket;
-- interval, the time interval in millis of the token bucket;
local key = KEYS[1]
local limit, interval, intervalPerPermit, refillTime = tonumber(ARGV[1]), tonumber(ARGV[2]), tonumber(ARGV[3]), tonumber(ARGV[4])

local currentTokens
local bucket = redis.call('hgetall', key)

if table.maxn(bucket) == 0 then
    -- first check if bucket not exists, if yes, create a new one with full capacity, then grant access
    currentTokens = limit
    redis.call('hset', key, 'lastRefillTime', refillTime)
elseif table.maxn(bucket) == 4 then
    -- if bucket exists, first we try to refill the token bucket

    local lastRefillTime, tokensRemaining = tonumber(bucket[2]), tonumber(bucket[4])

    if refillTime > lastRefillTime then
        -- if refillTime larger than lastRefillTime, we should refill the token buckets

        -- calculate the interval between refillTime and lastRefillTime
        -- if the result is bigger than the interval of the token bucket, refill the tokens to capacity limit
        -- else calculate how much tokens should be refilled
        local intervalSinceLast = refillTime - lastRefillTime
        if intervalSinceLast > interval then
            currentTokens = limit
            redis.call('hset', key, 'lastRefillTime', refillTime)
        else
            -- 通過floor下舍取整（0.6=0，5.1=5，5=5，-5.9=-6）
            local grantedTokens = math.floor(intervalSinceLast / intervalPerPermit)
            if grantedTokens > 0 then
                -- ajust lastRefillTime, we want shift left the refill time.
                local padMillis = math.fmod(intervalSinceLast, intervalPerPermit)
                -- 更為精確的設置上一次填充Token的時間（減掉fmod取余后的結果，相當於上面floor是對一個整數操作后得到了grantedTokens）
                redis.call('hset', key, 'lastRefillTime', refillTime - padMillis)
            end
            currentTokens = math.min(grantedTokens + tokensRemaining, limit)
        end
    else
        -- while refillTime not larger than lastRefillTime,
        -- it means some other operation later than this call made the call first.
        -- there is no need to refill the tokens.
        currentTokens = tokensRemaining
    end
else
    error("Size of bucket is " .. table.maxn(bucket) .. ", Should Be 0 or 4.")
end

assert(currentTokens >= 0)

if currentTokens == 0 then
    -- we didn't consume any keys
    redis.call('hset', key, 'tokensRemaining', currentTokens)
    return 0
else
    redis.call('hset', key, 'tokensRemaining', currentTokens - 1)
    return 1
end