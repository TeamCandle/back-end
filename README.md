# @ while testing


# API 명세

## Domain 
### `http://52.79.83.190`

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
### GET /profile/user

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
### POST /profile/user
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
### POST /profile/user   

### 응답
200_ok   
{
    "name": 이름,
    "gender": 성별,
    "age": 나이,
    "description": 설명 (현재 null),
    "image": 이미지 (현재 null)
}




