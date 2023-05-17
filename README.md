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
程式啟動後用瀏覽器訪問 `http://localhost:8080/LiteScheduler`
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
#### 然後新增 .properties 設定檔，這裡使用 `dev.properties` 為範例
> ```
> mysql.url=jdbc:mysql://localhost:3306/example
> mysql.username=sa
> mysql.password=sa
> ```
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
>
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
> 2. 在 src 內增加一個 `mybatis-mapper` 資料夾
> 3. 開啟 `pom.xml` 在 `<build>` 內 `<resources>` 中加入
> ```
> <resource> 
>   <directory>src/mybatis-mapper</directory>
>   <targetPath>mybatis-mapper</targetPath>
>   <includes>
>     <include>**/*.xml</include>
>   </includes>
> </resource>
> ```
> 4. 在你自己客製化的 package 內，增加兩個 package `model` 與 `mapper`，這裡使用 `com.custom.model` 與 `com.custom.mapper` 為範例
> 5. 使用 mybatis-generator 自動產生 `model class` 與 `mapper class`，這裡提供一個 Java Besed 的產生方式範例，但連線資訊需手動修改
> ```
> import java.io.File;
> import java.util.ArrayList;
> import java.util.HashSet;
> import java.util.List;
> import java.util.Set;
> import java.util.UUID;
> 
> import org.mybatis.generator.api.MyBatisGenerator;
> import org.mybatis.generator.api.ProgressCallback;
> import org.mybatis.generator.config.ColumnOverride;
> import org.mybatis.generator.config.CommentGeneratorConfiguration;
> import org.mybatis.generator.config.Configuration;
> import org.mybatis.generator.config.Context;
> import org.mybatis.generator.config.JDBCConnectionConfiguration;
> import org.mybatis.generator.config.JavaClientGeneratorConfiguration;
> import org.mybatis.generator.config.JavaModelGeneratorConfiguration;
> import org.mybatis.generator.config.JavaTypeResolverConfiguration;
> import org.mybatis.generator.config.ModelType;
> import org.mybatis.generator.config.SqlMapGeneratorConfiguration;
> import org.mybatis.generator.config.TableConfiguration;
> import org.mybatis.generator.internal.DefaultShellCallback;
> 
> public class MybatisGenerator {
> 
> 	public static void main(String[] args) throws Exception {
> 		startGenerate();
> 	}
> 
> 	/******************************************************************************/
> 	/******************************************************************************/
> 	/******************************************************************************/
> 	/******************************************************************************/
> 
> 	// 使用者工作目錄
> 	private static final String USER_DIR = System.getProperty("user.dir");
> 
> 	// java resource 位置
> 	private static final String TARGET_PROJECT = new File(USER_DIR, "src").getPath();
> 
> 	// 儲存的 package 名稱
> 	private static final String targetPackage = "com.custom";
> 	private static final String targetPackage_model = "model";
> 	private static final String targetPackage_mapper = "mapper";
> 	private static final String targetPackage_xml = "xml";
> 
> 	// 資料庫連線資訊
> 	private static final String driverClass = "com.mysql.cj.jdbc.Driver";
> 	private static final String connectionURL = "jdbc:mysql://localhost:3306/exampleDatabase";
> 	private static final String userId = "sa";
> 	private static final String password = "sa";
> 
> 	// 要產生的 Table
> 	private static final Set<TableInfo> tableInfos = new HashSet<>();
> 	static {
> 		addTable("exampleDatabase", "exampleTable");
> 	}
> 
> 	/******************************************************************************/
> 	/******************************************************************************/
> 	/******************************************************************************/
> 	/******************************************************************************/
> 
> 	private static void startGenerate() throws Exception {
> 		List<String> warnings = new ArrayList<>();
> 
> 		boolean overwrite = true;
> 
> 		Configuration config = createConfiguration("MyBatis3DynamicSql");
> 
> 		DefaultShellCallback callback = new DefaultShellCallback(overwrite);
> 
> 		MyBatisGenerator generator = new MyBatisGenerator(config, callback, warnings);
> 
> 		generator.generate(new ProgressCallback() {
> 
> 			@Override
> 			public void introspectionStarted(int totalTasks) {
> 
> 			}
> 
> 			@Override
> 			public void generationStarted(int totalTasks) {
> 			}
> 
> 			@Override
> 			public void saveStarted(int totalTasks) {
> 			}
> 
> 			@Override
> 			public void startTask(String taskName) {
> 				System.out.println(taskName);
> 			}
> 
> 			@Override
> 			public void done() {
> 				warnings.forEach(warning -> System.out.println(warning));
> 				System.out.println("Mybatis mapper generation completed.");
> 			}
> 
> 			@Override
> 			public void checkCancel() throws InterruptedException {
> 
> 			}
> 		});
> 
> 	}
> 
> 	private static Configuration createConfiguration(String targetRuntime) {
> 		Configuration config = new Configuration();
> 		config.addContext(createContext(targetRuntime));
> 		return config;
> 	}
> 
> 	private static Context createContext(String targetRuntime) {
> 
> 		Context context = new Context(ModelType.CONDITIONAL);
> 
> 		context.setCommentGeneratorConfiguration(createCommentGeneratorConfiguration());
> 
> 		context.setId(UUID.randomUUID().toString());
> 
> 		context.setTargetRuntime(targetRuntime);
> 
> 		context.setJdbcConnectionConfiguration(createJDBCConnectionConfiguration());
> 
> 		context.setJavaTypeResolverConfiguration(createJavaTypeResolverConfiguration());
> 
> 		context.setJavaModelGeneratorConfiguration(createJavaModelGeneratorConfiguration());
> 
> 		context.setSqlMapGeneratorConfiguration(createSqlMapGeneratorConfiguration());
> 
> 		context.setJavaClientGeneratorConfiguration(createJavaClientGeneratorConfiguration());
> 
> 		tableInfos.forEach(tableInfo -> {
> 			context.addTableConfiguration(createTableConfiguration(context, tableInfo.schema, tableInfo.tableName, tableInfo.columnOverrides));
> 		});
> 
> 		return context;
> 	}
> 
> 	private static JDBCConnectionConfiguration createJDBCConnectionConfiguration() {
> 		JDBCConnectionConfiguration jdbcConnectionConfiguration = new JDBCConnectionConfiguration();
> 		jdbcConnectionConfiguration.setDriverClass(driverClass);
> 		jdbcConnectionConfiguration.setConnectionURL(connectionURL);
> 		jdbcConnectionConfiguration.setUserId(userId);
> 		jdbcConnectionConfiguration.setPassword(password);
> 		jdbcConnectionConfiguration.addProperty("nullCatalogMeansCurrent", "true");
> 		return jdbcConnectionConfiguration;
> 	}
> 
> 	private static JavaTypeResolverConfiguration createJavaTypeResolverConfiguration() {
> 		JavaTypeResolverConfiguration typeResolverConfiguration = new JavaTypeResolverConfiguration();
> 		typeResolverConfiguration.addProperty("forceBigDecimals", "false");
> 		return typeResolverConfiguration;
> 	}
> 
> 	private static CommentGeneratorConfiguration createCommentGeneratorConfiguration() {
> 		CommentGeneratorConfiguration commentGeneratorConfiguration = new CommentGeneratorConfiguration();
> 		commentGeneratorConfiguration.addProperty("suppressAllComments", "true");
> 		return commentGeneratorConfiguration;
> 	}
> 
> 	/**
> 	 * Model Class
> 	 * 
> 	 * @return
> 	 */
> 	private static JavaModelGeneratorConfiguration createJavaModelGeneratorConfiguration() {
> 		JavaModelGeneratorConfiguration javaModelGeneratorConfiguration = new JavaModelGeneratorConfiguration();
> 		javaModelGeneratorConfiguration.setTargetPackage(targetPackage + "." + targetPackage_model);
> 		javaModelGeneratorConfiguration.setTargetProject(TARGET_PROJECT);
> 		javaModelGeneratorConfiguration.addProperty("enableSubPackages", "true");
> 		javaModelGeneratorConfiguration.addProperty("trimStrings", "true");
> 		return javaModelGeneratorConfiguration;
> 	}
> 
> 	/**
> 	 * Mapper XML
> 	 * 
> 	 * @return
> 	 */
> 	private static SqlMapGeneratorConfiguration createSqlMapGeneratorConfiguration() {
> 		SqlMapGeneratorConfiguration sqlMapGeneratorConfiguration = new SqlMapGeneratorConfiguration();
> 		sqlMapGeneratorConfiguration.setTargetPackage(targetPackage + "." + targetPackage_xml);
> 		sqlMapGeneratorConfiguration.setTargetProject(TARGET_PROJECT);
> 
> 		sqlMapGeneratorConfiguration.addProperty("enableSubPackages", "true");
> 		return sqlMapGeneratorConfiguration;
> 	}
> 
> 	/**
> 	 * Mapper Class
> 	 * 
> 	 * @return
> 	 */
> 	private static JavaClientGeneratorConfiguration createJavaClientGeneratorConfiguration() {
> 		JavaClientGeneratorConfiguration javaClientGeneratorConfiguration = new JavaClientGeneratorConfiguration();
> 		javaClientGeneratorConfiguration.setTargetPackage(targetPackage + "." + targetPackage_mapper);
> 		javaClientGeneratorConfiguration.setConfigurationType("XMLMAPPER");
> 		javaClientGeneratorConfiguration.setTargetProject(TARGET_PROJECT);
> 		javaClientGeneratorConfiguration.addProperty("enableSubPackages", "true");
> 		return javaClientGeneratorConfiguration;
> 	}
> 
> 	/**
> 	 * table
> 	 * 
> 	 * @param context
> 	 * @param schema
> 	 * @param tableName
> 	 * @return
> 	 */
> 	private static TableConfiguration createTableConfiguration(Context context, String schema, String tableName, ColumnOverride... columnOverrides) {
> 		TableConfiguration tableConfiguration = new TableConfiguration(context);
> 		tableConfiguration.setSchema(schema);
> 		tableConfiguration.setTableName(tableName);
> 		tableConfiguration.setDomainObjectName(tableName);
> 		for (ColumnOverride columnOverride : columnOverrides) {
> 			tableConfiguration.addColumnOverride(columnOverride);
> 		}
> 		tableConfiguration.addProperty("useActualColumnNames", "true");
> 		return tableConfiguration;
> 	}
> 
> 	private static ColumnOverride ColumnOverride(String tableColumnName) {
> 		ColumnOverride columnOverride = new ColumnOverride(tableColumnName);
> 		return columnOverride;
> 	}
> 
> 	protected static ColumnOverride ColumnOverride(String tableColumnName, String javaProperty) {
> 		ColumnOverride columnOverride = ColumnOverride(tableColumnName);
> 		columnOverride.setJavaProperty(javaProperty);
> 		return columnOverride;
> 	}
> 
> 	protected static ColumnOverride ColumnOverride(String tableColumnName, Class<?> jdbcType, String javaProperty, Class<?> javaType) {
> 		ColumnOverride columnOverride = ColumnOverride(tableColumnName, javaProperty);
> 		columnOverride.setJavaType(javaType.getName());
> 		columnOverride.setJavaType(jdbcType.getName());
> 		return columnOverride;
> 	}
> 
> 	protected static ColumnOverride ColumnOverride(String tableColumnName, Class<?> jdbcType, String javaProperty, Class<?> javaType, boolean columnNameDelimited) {
> 		ColumnOverride columnOverride = ColumnOverride(tableColumnName, jdbcType, javaProperty, javaType);
> 		columnOverride.setColumnNameDelimited(columnNameDelimited);
> 		return columnOverride;
> 	}
> 
> 	private static void addTable(String schema, String tableName, ColumnOverride... columnOverrides) {
> 		TableInfo tableInfo = new TableInfo();
> 		tableInfo.schema = schema;
> 		tableInfo.tableName = tableName;
> 		tableInfo.columnOverrides = columnOverrides;
> 		tableInfos.add(tableInfo);
> 	}
> 
> 	private static class TableInfo {
> 		String schema;
> 		String tableName;
> 		ColumnOverride[] columnOverrides;
> 	}
> }
> ```
> 6. 在 src 底下新增一個 `mybatis-config.xml` 設定檔
> ```
> <?xml version="1.0" encoding="UTF-8" ?>  
> <!DOCTYPE configuration PUBLIC "-//mybatis.org//DTD Config 3.0//EN"  
> "http://mybatis.org/dtd/mybatis-3-config.dtd">
> <configuration>
> 
> 	<!-- 配置mybatis的緩存，延遲加載等等一系列屬性 -->
> 	<settings>
> 		<!-- mybatis 3 的所有參數 請參考 https://mybatis.org/mybatis-3/zh/configuration.html -->
> 
> 		<!-- 全局映射器啟用緩存 -->
> 		<setting name="cacheEnabled" value="false" />
> 
> 		<!-- 查詢時，關閉關聯對象即時加載以提高性能 -->
> 		<setting name="lazyLoadingEnabled" value="true" />
> 
> 		<!-- 設置關聯對象加載的形態，此處為按需加載字段(加載字段由SQL指 定)，不會加載關聯表的所有字段，以提高性能 -->
> 		<setting name="aggressiveLazyLoading" value="false" />
> 
> 		<!-- 對於未知的SQL查詢，允許返回不同的結果集以達到通用的效果 -->
> 		<setting name="multipleResultSetsEnabled" value="true" />
> 
> 		<!-- 允許使用列標籤代替列名 -->
> 		<setting name="useColumnLabel" value="true" />
> 
> 		<!-- 給予被嵌套的resultMap以字段-屬性的映射支持 -->
> 		<setting name="autoMappingBehavior" value="FULL" />
> 
> 		<!-- 對於批量更新操作緩存SQL以提高性能 (SIMPLE REUSE BATCH) -->
> 		<setting name="defaultExecutorType" value="SIMPLE" />
> 
> 		<!-- 數據庫超過2500秒仍未響應則超時 -->
> 		<setting name="defaultStatementTimeout" value="2500" />
> 
> 		<!--null set data in hashmap" -->
> 		<setting name="callSettersOnNulls" value="true" />
> 
> 		<!-- 當 一筆 row data 為空的時候，還是回傳整個物件，而非 null ,mybatis 3.4.2 版後才會有該參數 -->
> 		<setting name="returnInstanceForEmptyRow" value="true" />
> 
> 	</settings>
> 
> 
> 	<plugins>
> 		<!-- 註冊 mybatis 分頁攔截器，實現物理分頁 -->
> 		<plugin interceptor="com.github.pagehelper.PageInterceptor">
> 
> 			<!-- oracle,mysql,mariadb,sqlite,hsqldb,postgresql,db2,sqlserver,informix -->
> 			<!-- <property name="dialect" value="sqlserver"/> -->
> 
> 			<!-- 該參數默認為false -->
> 			<!-- 設置為true時，會將RowBounds第一個參數offset當成pageNum頁碼使用 -->
> 			<!-- 和startPage中的pageNum效果一樣 -->
> 			<property name="offsetAsPageNum" value="true" />
> 
> 			<!-- 該參數默認為false -->
> 			<!-- 設置為true時，使用RowBounds分頁會進行count查詢 -->
> 			<property name="rowBoundsWithCount" value="true" />
> 
> 			<!-- 設置為true時，如果pageSize=0或者RowBounds.limit = 0就會查詢出全部的結果 -->
> 			<!-- （相當於沒有執行分頁查詢，但是返回結果仍然是Page類型） -->
> 			<property name="pageSizeZero" value="true" />
> 
> 			<!-- 3.3.0版本可用 - 分頁參數合理化，默認false禁用 -->
> 			<!-- 啟用合理化時，如果pageNum<1會查詢第一頁，如果pageNum>pages會查詢最後一頁 -->
> 			<!-- 禁用合理化時，如果pageNum<1或pageNum>pages會返回空數據 -->
> 			<property name="reasonable" value="false" />
> 
> 			<!-- 3.5.0版本可用 - 為了支持startPage(Object params)方法 -->
> 			<!-- 增加了一個`params`參數來配置參數映射，用於從Map或ServletRequest中取值 -->
> 			<!-- 可以配置pageNum,pageSize,count,pageSizeZero,reasonable,orderBy,不配置映射的用默認值 -->
> 			<!-- 不理解該含義的前提下，不要隨便複製該配置 -->
> 			<property name="params" value="pageNum=start;pageSize=limit;" />
> 
> 			<!-- 支持通過Mapper接口參數來傳遞分頁參數 -->
> 			<property name="supportMethodsArguments" value="true" />
> 
> 			<!-- always總是返回PageInfo類型,check檢查返回類型是否為PageInfo,none返回Page -->
> 			<property name="returnPageInfo" value="check" />
> 		</plugin>
> 	</plugins>
> 
> 
> 	<!-- 全局別名設置，在映射文件中只需寫別名，而不必寫出整個類路徑 -->
> 	<mappers>
> 		<package name="com.custom.mapper" />
> 	</mappers>
> 
> </configuration>  
> ```
#### Spring MyBatis Java Based Configuration
> 在你自己客製化的 package 內任一位置建立一個 CustomConfiguration.java
> ```
> import java.io.IOException;
> 
> import javax.sql.DataSource;
> 
> import org.apache.commons.dbcp2.BasicDataSource;
> import org.mybatis.spring.SqlSessionFactoryBean;
> import org.mybatis.spring.SqlSessionTemplate;
> import org.mybatis.spring.annotation.MapperScan;
> import org.springframework.beans.factory.annotation.Autowired;
> import org.springframework.beans.factory.annotation.Qualifier;
> import org.springframework.context.annotation.Bean;
> import org.springframework.context.annotation.Configuration;
> import org.springframework.core.io.ClassPathResource;
> import org.springframework.core.io.Resource;
> import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
> import org.springframework.jdbc.datasource.DataSourceTransactionManager;
> 
> import lite.scheduler.cmp.CustomPropertyStored;
> 
> @Configuration
> @MapperScan(basePackages = "com.custom.mapper", sqlSessionFactoryRef = "mysqlSqlSessionFactory")
> public class CustomConfiguration {
> 
> 	@Autowired
> 	CustomPropertyStored customPropertyStored;
> 
> 	@Bean
> 	@Qualifier("mysqlTransactionManager")
> 	DataSourceTransactionManager mysqlTransactionManager() {
> 		DataSourceTransactionManager dm = new DataSourceTransactionManager();
> 		dm.setDataSource(mysqlDataSource());
> 		return dm;
> 	}
> 
> 	@Bean
> 	@Qualifier("mysqlSqlSessionFactory")
> 	SqlSessionFactoryBean mysqlSqlSessionFactory() throws IOException {
> 		SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
> 		sqlSessionFactoryBean.setDataSource(mysqlDataSource());
> 		sqlSessionFactoryBean.setConfigLocation(new ClassPathResource("mybatis-config.xml"));
> 		Resource[] mapperLocations = new PathMatchingResourcePatternResolver().getResources("classpath:mybatis-mapper/*.xml, classpath:mybatis-mapper/**/*.xml");
> 		sqlSessionFactoryBean.setMapperLocations(mapperLocations);
> 		return sqlSessionFactoryBean;
> 	}
> 
> 	@Bean
> 	@Qualifier("mysqlSqlSessionTemplate")
> 	SqlSessionTemplate mysqlSqlSessionTemplate() throws IOException, Exception {
> 		return new SqlSessionTemplate(mysqlSqlSessionFactory().getObject());
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
> 
> }
> ```
#### Spring MyBatis XML Based Configuration
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
> 	    <property name="dataSource" ref="mysqlDataSource" />
> 	</bean>
> 	
> 	<bean class="org.mybatis.spring.mapper.MapperScannerConfigurer">
> 	    <property name="basePackage" value="com.custom.mapper" />
> 	    <property name="sqlSessionFactoryBeanName" value="mysqlSqlSessionFactory" />
> 	</bean>
> 	
> 	<bean id="mysqlSqlSessionFactory" class="org.mybatis.spring.SqlSessionFactoryBean">
> 	    <property name="dataSource" ref="mysqlDataSource" />
> 	    <property name="configLocation" value="classpath:mybatis-config.xml" />
> 	    <property name="mapperLocations">
> 			<list>
> 				<value>classpath:mybatis-mapper/*.xml</value>
> 				<value>classpath:mybatis-mapper/**/*.xml</value>
> 			</list>
> 		</property>
> 	</bean>
> 	
> 	<bean id="mysqlSqlSessionTemplate" class="org.mybatis.spring.SqlSessionTemplate">
> 		<constructor-arg index="0" ref="mysqlSqlSessionFactory" />
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
>  		<property name="removeAbandonedOnMaintenance" value="true" />
> 	</bean>
> 	 
> </beans>
> ```
## 工作排程開發
在你自己客製化的 package 建立一支 Class，這裡使用 `CustomExampleTask.java` 為範例。
> ```
>   import org.springframework.stereotype.Component;
> 
>   import lite.scheduler.cmp.ExecuteParamenter;
>   import lite.scheduler.cmp.MessageWriter;
>   import lite.scheduler.cmp.ScheduleTask;
>   import lite.scheduler.cmp.TransactionManagers;
>   import lombok.extern.slf4j.Slf4j;
> 
>   @Slf4j
>   @Component
>   @TransactionManagers({ "mysqlTransactionManager"})
>   public class CustomExampleTask implements ScheduleTask {
> 
>     @Override
>     public void execute(ExecuteParamenter executeParamenter, MessageWriter messageWriter) throws Exception {
>       log.info("Hello World");
>     }
> 
> }
> 
> ```
> 實作 `lite.scheduler.cmp.ScheduleTask`，並在上方標記 `@Component`。
> 
> 關於 `@TransactionManagers`，此註記是告知該工作需要使用那些`事務交易管理器`。
> 
> 如果該排程未標記 `@TransactionManagers`，系統將會使用所有有被定義的 `TransactionManager`，包含 LiteScheduler 本身使用的 'coreTransactionManager'。
> 
> 為了效能最佳化，建議是在所有的`排程 Class`上方註記 `@TransactionManagers`。
> 
> 另外，`execute(ExecuteParamenter executeParamenter, MessageWriter messageWriter)` 中的第一個參數 `ExecuteParamenter executeParamenter`，
>
> 可以取得「全域參數」與「任務參數 」
> ```
> Map<String, String> globleParameterMap = executeParamenter.getGlobleParameterMap();
> Map<String, String> taskParameterMap = executeParamenter.getTaskParameterMap();
> ```
> 或是可以直接取值
> ```
> String data1 = executeParamenter.getGlobleParamenter("key");
> String data2 = executeParamenter.getTaskParamenter("key");
> ```
> 但要注意的是，ParameterMap 是 `unmodifiableMap` 不允許修改。
> 
> 關於第二個參數 `MessageWriter messageWriter`，此物件是專門寫入訊息至「任務歷程」內，且與工作內的 Transaction 分離，就算該工作拋出例外導致 rollback，該 messageWriter 仍會儲存。
## 部屬說明
### 補充說明
> 在 `application.properties` 內的 `spring.application.name` 會反應至 url 上，也就是說會註冊至 `context-path`，
> 
> 如果將 `spring.application.name` 設置為空值 `spring.application.name=`，這時在 Eclipse 啟動時的首頁 url 則會變成 `http://localhost:8080`。
>
> 但如果是部屬 WAR 檔至 Tomcat 時，`context-path` 是取決於 WAR 檔的檔案名稱，不會受 `spring.application.name` 影響。
>   
> 如要部屬 WAR 檔的話就在 Eclipse 上對著專案按下右鍵 > 選擇 Run As > Maven Build 。 在 Goals 欄位上，輸入 `clean package` 即可。
>
> 如果要變更 WAR 檔的檔案名稱，開啟 `pom.xml` 找到 '<finalName>LiteScheduler</finalName>'，將 `finalName` 內的值改成你希望的檔名。
### 部屬設定檔  
> 前面「開發說明」中的第一項 「基本設定」有提到，`file:${lite.scheduler.work.dir}/prd.properties` 在部屬 WAR 檔至 Tomcat 環境指向 `System.getProperty("catalina.base")`。
>
> 在 Tomcat 目錄底下(與`bin、conf、lib、logs、webapps`同一層)，建立 `prd.properties`，並將正式環境的設定放入。
# 感謝您耐心看完






