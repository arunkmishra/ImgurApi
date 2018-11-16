# Challenge
--
### Tech Stack
```
1. scala, scalatest
2. PlayFramework
```
### How to run test
```
> cd leadiq
> sbt test 
```

### How to start
```
> cd leadiq
> sbt run 

//sbt run task start http server at 9000 port
```
### ImageUpload api
```
> http://localhost:9000/v1/images/upload

Required: Json body is needed with urls to be uploaded to imgur
Result: Json with jobId

```

### Get upload job status api
```
> http://localhost:9000/v1/images/upload/:jobId

Required: JobId for which result is reuired
Return: Json with all details of that job.
```

### Get list of all uploaded image links api
```
> http://localhost:9000/v1/images

Required: Nothing
Result: Json with all imgur links
```

### Assumptions
```
1. waiting time is 20 seconds
2. Urls provided while uploading should be web urls.
```

### How to run App in docker
```
> docker build -t"arun/challenge" .
> docker run -i -p 9000:9000 "arun/challenge" sbt run

//task will start at 9000 port
```