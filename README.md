# Lite Scheduler
![ICO](https://github.com/DerrekTseng/LiteScheduler/assets/32017127/c76b2298-a389-42ef-840d-66638803c44d)

## 程式介紹
這是一個由 Spring Boot Starter Parent 2.7.11 與 Quartz 搭建而成的排程伺服器模組，並使用 Maven 打包成 WAR 檔部屬在 Tomcat 中，並且支持三種資料庫連線方式：
1. Spring Data JDBC Template
1. Spring Data JAP Hibernate
1. Spring MyBatis

> 所使用到的套件如下
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
> 4. 任務明細，可以編輯部分的資料且可以設定「任務參數」讓該任務排程可以讓參數動態化
>> ![04](https://github.com/DerrekTseng/LiteScheduler/assets/32017127/97e1dc20-5cdb-4034-8fd7-bd8b2dbdec09)
>
> 5. 全域參數，也可以在任務列表左上角中設定全域的參數，讓所有任務排程都可以讀取到
>> ![05](https://github.com/DerrekTseng/LiteScheduler/assets/32017127/1995f166-e2ab-4d7d-a451-ffe65c4ff38a)
>
> 6. 任務歷程，可以查看該任務的執行狀況，該次執行所使用的參數等等
>> ![06](https://github.com/DerrekTseng/LiteScheduler/assets/32017127/1e3f31bd-b270-44e5-9bdb-98345232ae8f)





