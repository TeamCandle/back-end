# @ while testing


# API 명세

## Host
### `http://13.209.220.187`

모든 요청은 헤더에 Authorization을 포함 해야함 (로그아웃, accessToken 재발급 제외)   
Authorization: Bearer ${accessToken value}

### 응답 
헤더에 Authorization이 없을 경우  
401_Unauthorized  
  {
    
    "error": "Unauthorized",
    "message": "Not logged in",
    "status": 401
  }

유효하지 않은 accessToken일 경우   
401_Unauthorizaed  
  {
    
    "error": "Unauthorized",
    "message": "Invalid or expried token",
    "status": 401
  }


## 로그인
### GET /user/login/kakao

### 응답
200_ok  
{
  
  "accessToken": `${accessToken value}`,  
  "refreshToken": `${refreshToken value}`  
}


## 로그아웃 
### DELETE /user/logout

### 응답 
200_ok  
text: "logout success"   


▶ 정상 수행 이후 클라이언트 단에서는 accessToken 값과 refreshToken 값을 제거하고 로그아웃 진행 


## accessToken 재발급 
### POST /user/accessToken
- body  
{
    "refreshToken": ${refreshToken value}
}

### 응답 
200_ok   
{
  
    "accessToken": ${accessToken value}
}

### Exception 
request body에 refreshToken 값이 없을 경우    
401_Unauthorized   
{
  
    "error": "Unauthorized",
    "message": "can't find refreshToken",
    "status": 401
}   

refreshToken이 만료됐을 경우   
401_unauthorized   
{
  
    "error": "Unauthorized",
    "message": "expried token",
    "status": 401
}   


▶ expried token 토큰의 경우 클라이언트 단에서는 로그아웃 진행후 이용자에게 다시 로그인을 요청한다.


## 유저 프로필 정보
### GET /profile/user   

### 응답
200_ok   
{  
  
    "name": 이름,
    "gender": 성별,
    "age": 나이,
    "description": 설명,
    "image": 이미지(base 64 byte code),  
    애견 리스트
    "dogList": [    
        {  
            "id": 애견 id(조회시 이용)
            "name: 이름,
            "gender": 성별, 
            "image": 이미지(base 64 byte code)
        },  
        ...  
    ]  
}  


## 유저 프로필 설명 변경
### PATCH /profile/user 
- body  
{
  "description": ${description value}
}

### 응답
200_ok   
text: "success change"   


## 유저 프로필 이미지 변경
### PATCH /profile/user 
- content-type: multipart/form-data
     
  -----------------  
  Content-Disposition: form-data; name="image"; filename=%{file name}  
  Content-Type: image/jpeg

  ${binary data}
  
  -----------------

### 응답
200_ok   
text: "success change"   


## 애견 프로필 조회
### GET /profile/dog?id=${애견 id} 

### 응답
200_ok   
{

    "name": 이름,
    "owner": 주인이름,
    "gender": 성별,
    "neutered": 중성화 여부,
    "age": 나이,
    "size": 크기
    "weight": 무게,
    "breed": 견종,
    "description": 설명,
    "image": 이미지(base 64 byte code)
}


잘못된 ID일 경우   
400_bad request   
{
  
    "error": "bad request",
    "message": "can't find dog",
    "status": 400
}   



## 애견 프로필 등록
### POST /profile/dog
- content-type: multipart/form-data
  
  ----------------  
  Content-Disposition: form-data; name="name"
  
  ${name value} :required
  
  ----------------
  Content-Disposition: form-data; name="gender"

  ${gender value} :required
  
  ----------------
  Content-Disposition: form-data; name="neutered"

  ${neutered value}
  
  ----------------
  Content-Disposition: form-data; name="age"

  ${age value} :required
  
  ----------------
  Content-Disposition: form-data; name="size"

  ${size value} :required
  
  ----------------
  Content-Disposition: form-data; name="weight"

  ${weight value} :required
  
  ----------------
  Content-Disposition: form-data; name="breed"

  ${breed value} :required
  
  ----------------
  Content-Disposition: form-data; name="description"

  ${description calue}
  
  ----------------
  Content-Disposition: form-data; name="image"; filename=%{file name}  
  Content-Type: image/jpeg

  ${binary data}
  
  -----------------

### 응답
200_ok   
{

    "id": ${애견 id}
}


## 애견 프로필 변경
### PATCH /profile/dog
- content-type: multipart/form-data

  ----------------  
  Content-Disposition: form-data; name="id"
  
  ${애견 id} :required
  
  ----------------
  
  ----------------  
  Content-Disposition: form-data; name="name"
  
  ${name value} :required
  
  ----------------
  Content-Disposition: form-data; name="gender"

  ${gender value} :required
  
  ----------------
  Content-Disposition: form-data; name="neutered"

  ${neutered value}
  
  ----------------
  Content-Disposition: form-data; name="age"

  ${age value} :required
  
  ----------------
  Content-Disposition: form-data; name="size"

  ${size value} :required
  
  ----------------
  Content-Disposition: form-data; name="weight"

  ${weight value} :required
  
  ----------------
  Content-Disposition: form-data; name="breed"

  ${breed value} :required
  
  ----------------
  Content-Disposition: form-data; name="description"

  ${description calue}
  
  ----------------
  Content-Disposition: form-data; name="image"; filename=%{file name}  
  Content-Type: image/jpeg

  ${binary data}
  
  -----------------

### 응답
200_ok   
text: "success change"   

required 값이 입력이 안됐거나 잘못된 값일 경우 
에러 리스트 
[ 

    {
        "defaultMessage": ${에러 메시지},
        "field": ${잘못된 필드}
        "rejectedValue": ${거부된 값(입력된 값)}.
        "code": ${에러 종류}
      }
]

▶ 에러가 났을 경우 거부된 값을 적절한 필드에 채우고 에러메시지와 함께 입력폼 재반환 (현재는 에러 메시지가 유효하지 않음)



## 애견 프로필 삭제
### DELETE /profile/dog?id=${애견 id}

### 응답
200_ok   
text: "success change"  


잘못된 ID일 경우   
400_bad request   
{
  
    "error": "bad request",
    "message": "can't find dog",
    "status": 400
}   
