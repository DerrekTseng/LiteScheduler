# Lite Scheduler
![ICO](https://github.com/DerrekTseng/LiteScheduler/assets/32017127/c76b2298-a389-42ef-840d-66638803c44d)

## 程式介紹
這是一個由 Spring Boot Starter Parent 2.7.11 與 Quartz 搭建而成的排程伺服器模組，並使用 Maven 打包成 WAR 檔部屬在 Tomcat 中，並且支持三種資料庫連線方式：
1. Spring Data JDBC Template
1. Spring Data JAP Hibernate
1. Spring MyBatis

### 所使用到的套件如下
> 
> 環境
>> 1. Java 11
>> 2. Tomcat 9.0
> 
> 前端套件
>> 1. bootstrap@5.0.2
>> 1. bootstrap-icons@1.9.0
>
> 後端套件
>> 1. spring-boot-starter
>> 1. spring-boot-starter-web
>> 1. spring-boot-starter-log4j2
>> 1. spring-boot-starter-validation
>> 1. spring-boot-starter-thymeleaf
>> 1. spring-boot-starter-data-jpa
>> 1. hibernate-entitymanager
>> 1. h2
>> 1. lombok
>> 1. quartz
>> 1. apache commons

## 功能介紹
程式啟動後用瀏覽器訪問`http://localhost:8080/LiteScheduler`
> 1. 主畫面，其中左上角的圖示功能為「新增排程」、「重新整理」與「全域參數」
>> ![01](https://github.com/DerrekTseng/LiteScheduler/assets/32017127/8a4bdd15-936f-4236-9015-f892f4283136)
> 
> 2. 新增排程，其中的「執行時間」使用 Quartz Cron Expression。「Task Class」是要被執行的 Class，例如 `com.custom.CustomExampleTask`
>> ![02](https://github.com/DerrekTseng/LiteScheduler/assets/32017127/e4e263a1-b6d9-4161-b8eb-32bd0391181f)
>
> 3. 按下「執行時間」最右邊的日曆按鈕則可以選擇執行時間
>> ![03](https://github.com/DerrekTseng/LiteScheduler/assets/32017127/a344e584-a56c-4719-a468-549fdfffb55d)
>
> 4. 任務明細，可以編輯部分的資料且可以設定「任務參數」讓該任務排程可以參數動態化
>> ![04](https://github.com/DerrekTseng/LiteScheduler/assets/32017127/97e1dc20-5cdb-4034-8fd7-bd8b2dbdec09)
>
> 5. 全域參數，也可以在任務列表左上角中設定全域的參數，讓所有任務排程都可以讀取到
>> ![05](https://github.com/DerrekTseng/LiteScheduler/assets/32017127/1995f166-e2ab-4d7d-a451-ffe65c4ff38a)
>
> 6. 任務歷程，可以查看該任務的執行狀況，該次執行所使用的參數等等
>> ![06](https://github.com/DerrekTseng/LiteScheduler/assets/32017127/1e3f31bd-b270-44e5-9bdb-98345232ae8f)

## 開發說明
### 1. 基本設定
#### 首先須在 src 中建立一個屬於自己客製化的 package，這裡使用 `com.custom` 為範例。
> 
> 開啟 `applicationContext.xml` 修改 `<context:component-scan base-package="com.custom" />` 內 base-package 的值為自己客製化的 package。
>
#### 緊接著修改 properties 設定檔位置
> ``` 
>	<util:list id="customProperties" value-type="org.springframework.core.io.Resource">
>		<value>classpath:/dev.properties</value>
>		<value>file:${lite.scheduler.work.dir}/prd.properties</value>
>	</util:list>
> ```
> 第一個 `classpath:/dev.properties` 會優先讀取。
> 
> 第二個 `file:${lite.scheduler.work.dir}/prd.properties` 內的 `lite.scheduler.work.dir` 參數則會指向
> 1. 在 Eclipse 環境指向 `System.getProperty("user.dir")`
> 2. 在部屬 WAR 檔至 Tomcat 環境指向 `System.getProperty("catalina.base")`
#### 再來開啟 `pom.xml` 加入 JDBC Driver，這裡使用 MySQL 為範例，如有使用其他 Driver 時也一併將依賴加入。
> ```
> <dependency>
>   <groupId>mysql</groupId>
>   <artifactId>mysql-connector-java</artifactId>
>   <version>8.0.29</version>
> </dependency>
> ```
### 2. 資料庫連線
這裡會依需講解 Java Based 或 XML Based 的個別設定方式
1. Spring Data JDBC Template
1. Spring Data JAP Hibernate
1. Spring MyBatis
#### 1. Spring Data JDBC Template
##### 前置設定
> 1. 開啟 `pom.xml` 加入依賴
> ```
> <dependency>
>    <groupId>org.springframework.boot</groupId>
>    <artifactId>spring-boot-starter-data-jdbc</artifactId>
> </dependency>
> ```
> 2. 開啟 `LiteSchedulerApplication.java` 在 class 上方 `@SpringBootApplication` 的 `exclude` 內加入 `JdbcTemplateAutoConfiguration.class` 
#### Spring Data JDBC Template Java Based Configuration
> 在你自己客製化的 package 內任一位置建立一個 CustomConfiguration.java
> ```
> import javax.sql.DataSource;
> 
> import org.apache.commons.dbcp2.BasicDataSource;
> import org.springframework.beans.factory.annotation.Autowired;
> import org.springframework.beans.factory.annotation.Qualifier;
> import org.springframework.context.annotation.Bean;
> import org.springframework.context.annotation.Configuration;
> import org.springframework.jdbc.core.JdbcTemplate;
> import org.springframework.jdbc.datasource.DataSourceTransactionManager;
> 
> import lite.scheduler.cmp.CustomPropertyStored;
> 
>  @Configuration
>  public class CustomConfiguration {
>
>    @Autowired
>    CustomPropertyStored customPropertyStored;
>
>    @Bean
>    @Qualifier("mysqlTransactionManager")
>    DataSourceTransactionManager mysqlTransactionManager() {
>      DataSourceTransactionManager dm = new DataSourceTransactionManager();
>      dm.setDataSource(mysqlDataSource());
>      return dm;
>    }
>
>    @Bean
>    @Qualifier("mysqlJdbcTemplate")
>    JdbcTemplate mysqlJdbcTemplate() {
>      return new JdbcTemplate(mysqlDataSource());
>    }
>
>    @Bean
>    @Qualifier("mysqlDataSource")
>    DataSource mysqlDataSource() {
>      BasicDataSource dataSource = new BasicDataSource();
>      dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
>      dataSource.setUrl(customPropertyStored.getValue("mysql.url"));
>      dataSource.setUsername(customPropertyStored.getValue("mysql.username"));
>      dataSource.setPassword(customPropertyStored.getValue("mysql.password"));
>      dataSource.setMaxTotal(100);
>      dataSource.setValidationQuery("SELECT 1 FROM DUAL");
>      dataSource.setMaxConnLifetimeMillis(14400000);
>      dataSource.setTimeBetweenEvictionRunsMillis(600000);
>      dataSource.setRemoveAbandonedTimeout(60);
>      dataSource.setMinEvictableIdleTimeMillis(600000);
>      dataSource.setRemoveAbandonedOnBorrow(true);
>      dataSource.setRemoveAbandonedOnMaintenance(true);
>      return dataSource;
>    }
>  }
> ```
#### Spring Data JDBC Template XML Based Configuration
這裡有兩種方式
1. 第一種是直接在 `applicationContext.xml` 中加入
2. 第二種是建立一個新的 xml 檔案，然後在 `applicationContext.xml` 內加入 `<import resource="yourXmlConfig.xml" />`
> 這裡附上第二種方式的 xml 設定內容
> ```
> <?xml version="1.0" encoding="UTF-8"?>
> <beans default-autowire="byName"
> 	xmlns="http://www.springframework.org/schema/beans"
> 	xmlns:p="http://www.springframework.org/schema/p"
> 	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
> 	xsi:schemaLocation=
> 	"http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">
> 
> 	<bean id="mysqlTransactionManager" class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
> 		<property name="dataSource" ref="mysqlDataSource" />
> 	</bean>
> 	
> 	<bean id="mysqlJdbcTemplate" class="org.springframework.jdbc.core.JdbcTemplate">
> 		<constructor-arg ref="mysqlDataSource" />
> 	</bean>
>    
> 	<bean id="mysqlDataSource" class="org.apache.commons.dbcp2.BasicDataSource" destroy-method="close">
> 		<property name="driverClassName" value="com.mysql.cj.jdbc.Driver" />
> 		<property name="url" value="${mysql.url}" />
> 		<property name="username" value="${mysql.username}" />
> 		<property name="password" value="${mysql.password}" />
> 		<property name="maxTotal" value="100"/>
> 		<property name="validationQuery" value="SELECT 1 FROM DUAL"/>
> 		<property name="maxConnLifetimeMillis" value="14400000"/>
> 		<property name="timeBetweenEvictionRunsMillis" value="600000"/>
> 		<property name="removeAbandonedTimeout" value="60"/>
> 		<property name="minEvictableIdleTimeMillis" value="600000"/>
> 		<property name="removeAbandonedOnBorrow" value="true" />
> 		<property name="removeAbandonedOnMaintenance" value="true" />
> 	</bean>
> 	 
> </beans>
> ```
#### 2. Spring Data JAP Hibernate
##### 前置設定
> 1. 在你自己客製化的 package 內，增加兩個 package `entity` 與 `repo` ， 這裡使用 `com.custom.entity` 與 `com.custom.repo` 為範例
> 2. 在那兩個 package 內放入你的 entity class 與 repository class
#### Spring Data JAP Hibernate Java Based Configuration
> 在你自己客製化的 package 內任一位置建立一個 CustomConfiguration.java
> ```
> import java.util.Properties;
> 
> import javax.sql.DataSource;
> 
> import org.apache.commons.dbcp2.BasicDataSource;
> import org.springframework.beans.factory.annotation.Autowired;
> import org.springframework.beans.factory.annotation.Qualifier;
> import org.springframework.context.annotation.Bean;
> import org.springframework.context.annotation.Configuration;
> import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
> import org.springframework.orm.jpa.JpaTransactionManager;
> import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
> import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
> 
> import lite.scheduler.cmp.CustomPropertyStored;
> 
> @Configuration
> @EnableJpaRepositories(basePackages = "com.custom.repo", entityManagerFactoryRef = "mysqlEntityManagerFactory", transactionManagerRef = "mysqlTransactionManager")
> public class CustomConfiguration {
> 
> 	@Autowired
> 	CustomPropertyStored customPropertyStored;
> 
> 	@Bean
> 	@Qualifier("mysqEntityManagerFactory")
> 	LocalContainerEntityManagerFactoryBean mysqlEntityManagerFactory() {
> 		LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
> 		em.setDataSource(mysqlDataSource());
> 		em.setPackagesToScan("com.custom.entity");
> 		em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
> 
> 		Properties properties = new Properties();
> 		properties.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQL8Dialect");
> 		properties.setProperty("hibernate.format_sql", "false");
> 		properties.setProperty("hibernate.show_sql", "false");
> 		properties.setProperty("hibernate.hbm2ddl.auto", "update");
> 		em.setJpaProperties(properties);
> 		return em;
> 	}
> 
> 	@Bean
> 	@Qualifier("mysqlTransactionManager")
> 	JpaTransactionManager mysql_jpa_java_TransactionManager() {
> 		JpaTransactionManager transactionManager = new JpaTransactionManager();
> 		transactionManager.setEntityManagerFactory(mysqlEntityManagerFactory().getObject());
> 		return transactionManager;
> 	}
> 
> 	@Bean
> 	@Qualifier("mysqlDataSource")
> 	DataSource mysqlDataSource() {
> 		BasicDataSource dataSource = new BasicDataSource();
> 		dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
> 		dataSource.setUrl(customPropertyStored.getValue("mysql.url"));
> 		dataSource.setUsername(customPropertyStored.getValue("mysql.username"));
> 		dataSource.setPassword(customPropertyStored.getValue("mysql.password"));
> 		dataSource.setMaxTotal(100);
> 		dataSource.setValidationQuery("SELECT 1 FROM DUAL");
> 		dataSource.setMaxConnLifetimeMillis(14400000);
> 		dataSource.setTimeBetweenEvictionRunsMillis(600000);
> 		dataSource.setRemoveAbandonedTimeout(60);
> 		dataSource.setMinEvictableIdleTimeMillis(600000);
> 		dataSource.setRemoveAbandonedOnBorrow(true);
> 		dataSource.setRemoveAbandonedOnMaintenance(true);
> 		return dataSource;
> 	}
> }
> ```
#### Spring Data JAP Hibernate XML Based Configuration
這裡有兩種方式
1. 第一種是直接在 `applicationContext.xml` 中加入
2. 第二種是建立一個新的 xml 檔案，然後在 `applicationContext.xml` 內加入 `<import resource="yourXmlConfig.xml" />`
> 這裡附上第二種方式的 xml 設定內容
> ```
> <?xml version="1.0" encoding="UTF-8"?>
> <beans default-autowire="byName"
> 	xmlns="http://www.springframework.org/schema/beans"
> 	xmlns:p="http://www.springframework.org/schema/p"
> 	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
> 	xmlns:jpa="http://www.springframework.org/schema/data/jpa"
> 	xsi:schemaLocation=
> 	"http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
> 	 http://www.springframework.org/schema/data/jpa http://www.springframework.org/schema/data/jpa/spring-jpa.xsd">
> 
> 	<jpa:repositories base-package="com.custom.repo" entity-manager-factory-ref="mysql_jpa_xml_EntityManagerFactory" transaction-manager-ref="mysql_jpa_xml_TransactionManager"/>
> 
> 	<bean id="mysql_jpa_xml_TransactionManager" p:entityManagerFactory-ref="mysql_jpa_xml_EntityManagerFactory" class="org.springframework.orm.jpa.JpaTransactionManager"/>
> 	 
> 	<bean id="mysql_jpa_xml_EntityManagerFactory" class="org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean">
> 		<property name="dataSource" ref="mysql_jpa_xml_DataSource" />
> 		<property name="packagesToScan" value="com.custom.entity" />
> 		<property name="jpaVendorAdapter">
> 			<bean class="org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter" />
> 		</property>
> 		<property name="jpaProperties">
> 			<props>
> 				<prop key="hibernate.dialect">org.hibernate.dialect.MySQL8Dialect</prop>
> 				<prop key="hibernate.format_sql">false</prop>
> 				<prop key="hibernate.show_sql">false</prop>
> 				<prop key="hibernate.hbm2ddl.auto">validate</prop>
> 			</props>
> 		</property>
> 	</bean>
> 	
> 	<bean id="mysql_jpa_xml_DataSource" class="org.apache.commons.dbcp2.BasicDataSource" destroy-method="close">
> 		<property name="driverClassName" value="com.mysql.cj.jdbc.Driver" />
> 		<property name="url" value="${mysql.url}" />
> 		<property name="username" value="${mysql.username}" />
> 		<property name="password" value="${mysql.password}" />
> 		<property name="maxTotal" value="100"/>
> 		<property name="validationQuery" value="SELECT 1 FROM DUAL"/>
> 		<property name="maxConnLifetimeMillis" value="14400000"/>
> 		<property name="timeBetweenEvictionRunsMillis" value="600000"/>
> 		<property name="removeAbandonedTimeout" value="60"/>
> 		<property name="minEvictableIdleTimeMillis" value="600000"/>
> 		<property name="removeAbandonedOnBorrow" value="true" />
> 		<property name="removeAbandonedOnMaintenance" value="true" />
> 	</bean>
> 	 
> </beans>
> ```
### 3. Spring MyBatis
這裡會使用 mybatis-generator 與 mybatis-dynamic-sql 套件
##### 前置設定
> 1. 開啟 `pom.xml` 加入依賴
> ```
> <dependency>
>   <groupId>org.mybatis</groupId>
>   <artifactId>mybatis</artifactId>
>   <version>3.5.11</version>
> </dependency>
> <dependency>
>   <groupId>org.mybatis</groupId>
>   <artifactId>mybatis-spring</artifactId>
>   <version>2.1.0</version>
> </dependency>
> <dependency>
>   <groupId>org.mybatis.generator</groupId>
>   <artifactId>mybatis-generator-maven-plugin</artifactId>
>   <version>1.4.0</version>
> </dependency>
> <dependency>
>   <groupId>org.mybatis.dynamic-sql</groupId>
>   <artifactId>mybatis-dynamic-sql</artifactId>
>   <version>1.4.0</version>
> </dependency>
> <dependency>
>   <groupId>com.github.pagehelper</groupId>
>   <artifactId>pagehelper</artifactId>
>   <version>5.3.2</version>
> </dependency>
> ```
> 2. 在專案目錄內增加一個 mybatis-mapper 資料夾
> 3. 開啟 `pom.xml` 在 `<build>` 內 `<resources>` 中增加















