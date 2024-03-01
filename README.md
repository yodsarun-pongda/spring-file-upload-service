# yodsarun-spring-demo

## Step to set up project

1. Start the database service
```shell
docker compose up -d
```

2. Configuration on `application.yml` file
Please config your email/app password from Gmail service
- at the `email-config` property
  - `user-name`: the email sender
  - `app-password`: The security password had generated from email provider
3. Connect to database and Insert your mockup information to the `customer`, * email using sending email service
```mysql
insert into customer (name, email) values ('yodsarun', 'youremailaddress');
```

4. Start Interview application
5. Calling the http request as below
```http request
curl --location 'http://localhost:8080/api/v1/upload/file' \
--header 'auth-key: vIocOix5KE9EqkurBkTU' \
--form 'file=@"/path/to/file"' \
--form 'name="yodsarun"'
```