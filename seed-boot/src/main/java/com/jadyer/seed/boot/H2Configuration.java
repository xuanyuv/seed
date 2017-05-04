package com.jadyer.seed.boot;

import org.h2.tools.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import java.sql.SQLException;

/**
 * H2與StringBoot
 * --------------------------------------------------------------------------------------------------------------
 * H2的集成
 * 當引入了spring-boot-starter-jdbc和com.h2database依賴包之後，啟動時SpringBoot會自動配置H2並集成到項目中
 * 詳細介紹見http://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-sql.html#boot-features-embedded-database-support
 * 此時便可通過諸如com.alibaba.druid數據庫連接池連接到H2並操作數據庫了，連接時默認用戶名為sa，密碼為空，示例配置如下
 * spring.datasource.url=jdbc:h2:mem:jadyer;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
 * spring.datasource.username=sa
 * spring.datasource.password=
 * 若項目應用了JPA，則啟動項目時JPA會針對嵌入式數據庫自動創建@Entity標記的Table
 * 詳細介紹見http://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-sql.html#boot-features-creating-and-dropping-jpa-databases
 * --------------------------------------------------------------------------------------------------------------
 * H2控制台
 * 經測試，上面的集成已經可以通過程序進行CRUD了，但若想實時查看數據庫的操作結果，只能通過JPA.findAll()
 * 幸好H2提供了控制臺，不過需要我們顯式配置；配置方式大體有兩種，一是配置一個屬性，另一個是自己寫Configuration
 * 配置屬性主要有兩個
 * spring.h2.console.enabled=true（必要的）
 * spring.h2.console.path=/myh2（不必要的）
 * 若配置了path則訪問地址為http://127.0.0.1/boot/myh2/，否則就是默認的http://127.0.0.1/boot/h2-console/
 * 詳細介紹見http://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-sql.html#boot-features-sql-h2-console
 * 自己寫Configuration這裡也示例了兩種寫法
 * 其中一種就是本類的寫法，此時控制台地址為http://127.0.0.1:8082/
 * 另一種類的寫法如下所示，此时控制台地址為http://127.0.0.1/boot/h2-console/
 * Configuration
 * public class H2Configuration22{
 *     Bean
 *     public ServletRegistrationBean h2console(){
 *         ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean();
 *         servletRegistrationBean.setServlet(new WebServlet());
 *         servletRegistrationBean.addUrlMappings("/h2-console/*");
 *         servletRegistrationBean.addInitParameter("webAllowOthers", "true");
 *         return servletRegistrationBean;
 *     }
 * }
 * --------------------------------------------------------------------------------------------------------------
 * 注意，H2控制台的是否啟用與JDBC操作H2是沒有關聯的，即無論是否啟用控制台，都不影響JDBC操作H2，也不影響連接H2的用戶名密碼
 * --------------------------------------------------------------------------------------------------------------
 * H2的JDBC連接
 * <ul>
 *     <li>SpringBoot自動配置H2连接的默认用户名為sa，密码為空</li>
 *     <li>C:\Users\Jadyer\.h2.server.properties会被默认创建，里面记录了最后一次连接数据库的地址</li>
 *     <li>jdbcurl=jdbc:h2:~/abc表示数据存放在C:\Users\Jadyer\abc.mv.db，每次启动时会加载其数据</li>
 *     <li>jdbcurl=jdbc:h2:mem:abc表示数据放在内存，每次重启会丢失数据，后面可通过分号设置不同参数</li>
 * </ul>
 * --------------------------------------------------------------------------------------------------------------
 * H2的URL參數(jdbc:h2:mem:jadyer;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE)
 * <ul>
 *     <li>DB_CLOSE_DELAY=-1：仅对内存模式有效，表示JVM存活期间数据库一直在，否则默认关闭连接后数据库及数据会被清空</li>
 *     <li>DB_CLOSE_ON_EXIT=FALSE：Don't close the database when the VM exits</li>
 *     <li>MODE=MySQL：兼容模式，这里指定其兼容MySQL</li>
 *     <li>AUTO_SERVER=TRUE：自动混合模式，允许开启多个连接，该参数不支持内存模式</li>
 *     <li>AUTO_RECONNECT=TRUE：连接丢失后自动重连</li>
 *     <li>INIT=RUNSCRIPT FROM '~/create.sql'：Execute SQL on connection</li>
 * </ul>
 * --------------------------------------------------------------------------------------------------------------
 * Created by 玄玉<http://jadyer.cn/> on 2016/5/22 22:35.
 */
//@Configuration
//Only activate this in the "dev" profile
@Profile("dev")
public class H2Configuration {
    @Value("${h2.port.tcp:9092}")
    private String tcpPort;

    @Value("${h2.port.web:8082}")
    private String webPort;

    /**
     * TCP connection to connect with SQL clients to the embedded h2 database.
     * Connect to "jdbc:h2:tcp://localhost:9092/mem:testdb", username "sa", password empty.
     */
    @Bean
    @ConditionalOnExpression("${h2.tcp.enabled:false}")
    public Server createTcpServer() throws SQLException {
        return Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", tcpPort).start();
    }

    /**
     * Web console for the embedded h2 database.
     * Go to http://localhost:8082 and connect to the database "jdbc:h2:mem:testdb", username "sa", password empty.
     */
    @Bean
    @ConditionalOnExpression("${h2.web.enabled:true}")
    public Server createWebServer() throws SQLException {
        return Server.createWebServer("-web", "-webAllowOthers", "-webPort", webPort).start();
    }
}