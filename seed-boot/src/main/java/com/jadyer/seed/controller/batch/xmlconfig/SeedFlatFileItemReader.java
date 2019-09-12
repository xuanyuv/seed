package com.jadyer.seed.controller.batch.xmlconfig;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.item.ReaderNotOpenException;
import org.springframework.batch.item.file.BufferedReaderFactory;
import org.springframework.batch.item.file.DefaultBufferedReaderFactory;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.batch.item.file.LineCallbackHandler;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.NonTransientFlatFileException;
import org.springframework.batch.item.file.separator.RecordSeparatorPolicy;
import org.springframework.batch.item.file.separator.SimpleRecordSeparatorPolicy;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * 此类目的：读取时找不到文件，改为不抛异常（如此便不会影响到其它Step的执行）
 * Created by 玄玉<https://jadyer.cn/> on 2019/8/6 10:29.
 */
public class SeedFlatFileItemReader<T> extends FlatFileItemReader<T> {
    private static final Log logger = LogFactory.getLog(SeedFlatFileItemReader.class);

    // default encoding for input files
    public static final String DEFAULT_CHARSET = Charset.defaultCharset().name();

    private RecordSeparatorPolicy recordSeparatorPolicy = new SimpleRecordSeparatorPolicy();

    private Resource resource;

    private BufferedReader reader;

    private int lineCount = 0;

    private String[] comments = new String[] { "#" };

    private boolean noInput = false;

    private String encoding = DEFAULT_CHARSET;

    private LineMapper<T> lineMapper;

    private int linesToSkip = 0;

    private LineCallbackHandler skippedLinesCallback;

    private boolean strict = true;

    private BufferedReaderFactory bufferedReaderFactory = new DefaultBufferedReaderFactory();

    public SeedFlatFileItemReader() {
        setName(ClassUtils.getShortName(SeedFlatFileItemReader.class));
    }

    /**
     * In strict mode the reader will throw an exception on
     * {@link #open(org.springframework.batch.item.ExecutionContext)} if the input resource does not exist.
     * @param strict <code>true</code> by default
     */
    public void setStrict(boolean strict) {
        this.strict = strict;
    }

    /**
     * @param skippedLinesCallback will be called for each one of the initial skipped lines before any items are read.
     */
    public void setSkippedLinesCallback(LineCallbackHandler skippedLinesCallback) {
        this.skippedLinesCallback = skippedLinesCallback;
    }

    /**
     * Public setter for the number of lines to skip at the start of a file. Can be used if the file contains a header
     * without useful (column name) information, and without a comment delimiter at the beginning of the lines.
     *
     * @param linesToSkip the number of lines to skip
     */
    public void setLinesToSkip(int linesToSkip) {
        this.linesToSkip = linesToSkip;
    }

    /**
     * Setter for line mapper. This property is required to be set.
     * @param lineMapper maps line to item
     */
    public void setLineMapper(LineMapper<T> lineMapper) {
        this.lineMapper = lineMapper;
    }

    /**
     * Setter for the encoding for this input source. Default value is {@link #DEFAULT_CHARSET}.
     *
     * @param encoding a properties object which possibly contains the encoding for this input file;
     */
    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    /**
     * Factory for the {@link BufferedReader} that will be used to extract lines from the file. The default is fine for
     * plain text files, but this is a useful strategy for binary files where the standard BufferedReader from java.io
     * is limiting.
     *
     * @param bufferedReaderFactory the bufferedReaderFactory to set
     */
    public void setBufferedReaderFactory(BufferedReaderFactory bufferedReaderFactory) {
        this.bufferedReaderFactory = bufferedReaderFactory;
    }

    /**
     * Setter for comment prefixes. Can be used to ignore header lines as well by using e.g. the first couple of column
     * names as a prefix.
     *
     * @param comments an array of comment line prefixes.
     */
    public void setComments(String[] comments) {
        this.comments = new String[comments.length];
        System.arraycopy(comments, 0, this.comments, 0, comments.length);
    }

    /**
     * Public setter for the input resource.
     */
    @Override
    public void setResource(Resource resource) {
        this.resource = resource;
    }

    /**
     * Public setter for the recordSeparatorPolicy. Used to determine where the line endings are and do things like
     * continue over a line ending if inside a quoted string.
     *
     * @param recordSeparatorPolicy the recordSeparatorPolicy to set
     */
    public void setRecordSeparatorPolicy(RecordSeparatorPolicy recordSeparatorPolicy) {
        this.recordSeparatorPolicy = recordSeparatorPolicy;
    }

    /**
     * @return string corresponding to logical record according to
     * {@link #setRecordSeparatorPolicy(RecordSeparatorPolicy)} (might span multiple lines in file).
     */
    @Override
    protected T doRead() throws Exception {
        if (noInput) {
            return null;
        }

        String line = readLine();

        if (line == null) {
            return null;
        }
        else {
            try {
                return lineMapper.mapLine(line, lineCount);
            }
            catch (Exception ex) {
                throw new FlatFileParseException("Parsing error at line: " + lineCount + " in resource=["
                        + resource.getDescription() + "], input=[" + line + "]", ex, line, lineCount);
            }
        }
    }

    /**
     * @return next line (skip comments).getCurrentResource
     */
    private String readLine() {

        if (reader == null) {
            throw new ReaderNotOpenException("Reader must be open before it can be read.");
        }

        String line = null;

        try {
            line = this.reader.readLine();
            if (line == null) {
                return null;
            }
            lineCount++;
            while (isComment(line)) {
                line = reader.readLine();
                if (line == null) {
                    return null;
                }
                lineCount++;
            }

            line = applyRecordSeparatorPolicy(line);
        }
        catch (IOException e) {
            // Prevent IOException from recurring indefinitely
            // if client keeps catching and re-calling
            noInput = true;
            throw new NonTransientFlatFileException("Unable to read from resource: [" + resource + "]", e, line,
                    lineCount);
        }
        return line;
    }

    private boolean isComment(String line) {
        for (String prefix : comments) {
            if (line.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void doClose() throws Exception {
        lineCount = 0;
        if (reader != null) {
            reader.close();
        }
    }

    @Override
    protected void doOpen() throws Exception {
        Assert.notNull(resource, "Input resource must be set");
        Assert.notNull(recordSeparatorPolicy, "RecordSeparatorPolicy must be set");

        noInput = true;
        //if (!resource.exists()) {
        //    if (strict) {
        //        throw new IllegalStateException("Input resource must exist (reader is in 'strict' mode): " + resource);
        //    }
        //    logger.warn("Input resource does not exist " + resource.getDescription());
        //    return;
        //}
        if (!resource.exists() && strict) {
            logger.debug("业务数据文件 " + resource + " 不存在，系统自动跳过该Step");
            return;
        }

        if (!resource.isReadable()) {
            if (strict) {
                throw new IllegalStateException("Input resource must be readable (reader is in 'strict' mode): "
                        + resource);
            }
            logger.warn("Input resource is not readable " + resource.getDescription());
            return;
        }

        reader = bufferedReaderFactory.create(resource, encoding);
        for (int i = 0; i < linesToSkip; i++) {
            String line = readLine();
            if (skippedLinesCallback != null) {
                skippedLinesCallback.handleLine(line);
            }
        }
        noInput = false;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(lineMapper, "LineMapper is required");
    }

    @Override
    protected void jumpToItem(int itemIndex) throws Exception {
        for (int i = 0; i < itemIndex; i++) {
            readLine();
        }
    }

    private String applyRecordSeparatorPolicy(String line) throws IOException {

        String record = line;
        while (line != null && !recordSeparatorPolicy.isEndOfRecord(record)) {
            line = this.reader.readLine();
            if (line == null) {
                if (StringUtils.hasText(record)) {
                    // A record was partially complete since it hasn't ended but
                    // the line is null
                    throw new FlatFileParseException("Unexpected end of file before record complete", record, lineCount);
                }
                else {
                    // Record has no text but it might still be post processed
                    // to something (skipping preProcess since that was already
                    // done)
                    break;
                }
            }
            else {
                lineCount++;
            }
            record = recordSeparatorPolicy.preProcess(record) + line;
        }

        return recordSeparatorPolicy.postProcess(record);

    }
}