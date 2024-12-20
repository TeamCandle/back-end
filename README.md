# ▶애견 케어 매칭 서비스
![Screenshot_20240607_170037](https://github.com/TeamCandle/back-end/assets/69377952/f3e4aa33-584d-47c9-8a4b-62e796e074a7)

```
 목적: 애견을 키우나 돌볼 시간이 없는 사람과 키우기 싶지만 여력이 되지 않는 사람
       두 사람의 맞물리는 니즈를 연결시켜주는 것을 목적으로 하는 매칭 어플리케이션
```

# ▶목차 
- [기술](#기술)
- [시스템 구성](#시스템-구성)
- [DB 구성](#DB-구성)
- [디렉터리 구성](#디렉터리-구성)
- [서비스 개요](#서비스-개요)
- [성능 테스트 결과](#성능-테스트-결과)
- [API](#API)
  

# ▶기술 
```
- jdk 17: 개발 환경

- spring boot 3.2.x: 개발 프레임 워크

- mysql: 서버 db

- aws ec2, docker: 서버 배포

- rabbit mq: 웹소켓 stomp 외부 브로커

- redis: 인증 토큰 저장

- s3 bucket: 이미지 저장

- kakao login: 외부 로그인 연동

- firebase cloud messaging: 백그라운드 메시징
```

# ▶시스템 구성

![nc](https://github.com/TeamCandle/back-end/assets/69377952/5eb7774b-786e-4f17-9585-9da8774f861a)


# ▶DB 구성

![cc](https://github.com/TeamCandle/back-end/assets/69377952/0a9750e1-0cbd-414c-94e4-d3be99fccc23)


# ▶디렉터리 구성

```
carrotbow - root 디렉터리

chat - 채팅 서비스 디렉터리
error - 에러 디렉터리
external - 외부 서비스 모음 (rabbitMq, fcm, redis 등)
matching - 주요 매칭 서비스 디렉터리
profile - 프로필 디렉터리
security - 인증 디렉터리
```

 
# ▶서비스 개요 

## 0. 사용 시나리오 
![0](https://github.com/TeamCandle/back-end/assets/69377952/e1993e5d-c009-4e1f-bf0a-d7d0833af94a)


## 1. 로그인 및 프로필 관리 
![1](https://github.com/TeamCandle/back-end/assets/69377952/08ef8256-473f-4232-8394-ba32b5c44eae)
```
카카오 기반 로그인
```


## 2. 주위 요구사항 확인 
![2](https://github.com/TeamCandle/back-end/assets/69377952/f5c58cd4-d8d6-4cb1-aef7-4fa277d15cee)
```
일정 거리 내에 있는 요구사항 탐색 및 신청
```


## 3. 요구사항 등록
![3](https://github.com/TeamCandle/back-end/assets/69377952/1d325129-c675-4634-bac8-2ef264eeed23)
```
요구 사항 입력 및 등록, 받은 신청 확인 및 수락
```


## 4. 매칭 성사
![4](https://github.com/TeamCandle/back-end/assets/69377952/48fe4b2b-08fc-467b-a7ad-7f09ff4c697e)
```
성사된 매칭 확인 및 채팅
```

## 5. 매칭 완료 
![5](https://github.com/TeamCandle/back-end/assets/69377952/106017bf-8f74-47ff-8d37-4c3414441f2a)
```
요구사항 완료 확인 및 정산
```

## 6. 리뷰
![6](https://github.com/TeamCandle/back-end/assets/69377952/9e8f115a-14a0-4ba1-9693-057aedef988f)
```
리뷰 작성 및 확인 
```

# ▶성능 테스트
## 1. 시나리오 
![image](https://github.com/TeamCandle/back-end/assets/69377952/3052574e-0601-4adf-abac-b455e8f6f0d3)

## 2. 결과 
![image](https://github.com/TeamCandle/back-end/assets/69377952/d6c96901-804a-4523-9c20-e226a163d995)
![image](https://github.com/TeamCandle/back-end/assets/69377952/665973e3-d3f2-4e5c-9c3a-8d792ef0bcdc)




# ▶API 

## Host
### 배포 중단

모든 요청은 헤더에 Authorization을 포함 해야함 (로그아웃, accessToken 재발급 제외)   
Authorization: Bearer ${accessToken value}

### 응답 
헤더에 Authorization이 없을 경우  
  ```
  401_Unauthorized  
    {  
      "error": "Unauthorized",
      "message": "Not logged in",
      "status": 401
    }
```
유효하지 않은 accessToken일 경우   
```
401_Unauthorizaed  
  {
    "error": "Unauthorized",
    "message": "Invalid or expried token",
    "status": 401
  }
```

## 로그인
### GET /user/login/kakao

### 응답
```
200_ok  
{
  "accessToken": `${accessToken value}`,  
  "refreshToken": `${refreshToken value}`  
}
```

## 로그아웃 
### DELETE /user/logout

### 응답 
```
200_ok  
text: "logout success"   
```


## accessToken 재발급 
### POST /user/accessToken
```
- body
{
    "refreshToken": ${refreshToken value}
}
```

### 응답 
```
200_ok   
{
  "accessToken": `${accessToken value}`,  
  "refreshToken": `${refreshToken value}`  
}
```

### Exception 
request body에 refreshToken 값이 없을 경우    
```
401_Unauthorized   
{
    "error": "Unauthorized",
    "message": "can't find refreshToken",
    "status": 401
}   
```

refreshToken이 만료됐을 경우   
```
401_Unauthorized   
{ 
      "error": "Unauthorized",
      "message": "expried token",
      "status": 401
  }
```   


## 내 프로필 정보
### GET /profile/user/me  

### 응답
```
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
```

## 유저 프로필 정보
### GET /profile/user?id=${유저 Id}

### 응답
```
200_ok   
{  
    "id": 유저 id,  
    "name": 이름,
    "gender": 성별,
    "age": 나이,  
    "rating": 평점,  (기록이 없을 경우 -1 반환)  
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
``` 



## 유저 프로필 설명 변경
### PATCH /profile/user 
```
- body 
{
  "description": ${description value}
}
```

### 응답
```
200_ok   
text: "success change"   
```

## 유저 프로필 이미지 변경
### PATCH /profile/user 
```
- content-type: multipart/form-data
     
  -----------------  
  Content-Disposition: form-data; name="image"; filename=%{file name}  
  Content-Type: image

  ${binary data}
  
  -----------------
```
### 응답
```
200_ok   
text: "success change"   
```

## 애견 프로필 조회
### GET /profile/dog?id=${애견 id} 

### 응답
```
200_ok   
{
    "id": id,  
    "name": 이름,
    "owner": 견주 id,
    "gender": 성별,
    "neutered": 중성화 여부,
    "age": 나이,
    "size": 크기, (소형, 중형, 대형)
    "breed": 견종,
    "description": 설명,
    "image": 이미지(base 64 byte code)
}
```


## 애견 프로필 리스트 조회
### GET /profile/dog/list

### 응답
```
{  
    "dogs: [    
        {  
            "id": 애견 id(조회시 이용)
            "name: 이름,
            "gender": 성별, 
            "image": 이미지(base 64 byte code)
        },  
        ...  
    ]  
}
```

## 애견 프로필 등록
### POST /profile/dog
```
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
  Content-Type: image

  ${binary data}
  
  -----------------
```
### 응답
```
200_ok   
{
    "id": ${애견 id}
}
```

## 애견 프로필 변경
### PUT /profile/dog
```
- content-type: multipart/form-data

  ----------------  
  Content-Disposition: form-data; name="id"
  
  ${애견 id} :required
  
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

  ${size value} :required ("SMALL", "MEDIUM", "LARGE") 
  
  ----------------
  Content-Disposition: form-data; name="breed"

  ${breed value} :required
  
  ----------------
  Content-Disposition: form-data; name="description"

  ${description calue}
  
  ----------------
  Content-Disposition: form-data; name="image"; filename=%{file name}  
  Content-Type: image

  ${binary data}
  
  -----------------
```
### 응답
```
200_ok   
text: "success change"   
```

### ◆ multipart/form-data 입력에서 required 값이 입력이 안됐거나 잘못된 값일 경우 응답
```
에러 리스트 
[ 
    {
        "message": ${에러 메시지},
        "field": ${잘못된 필드},
        "rejectedValue": ${거부된 값(입력된 값)},
        "code": ${에러 종류}
      }
]
```


## 애견 프로필 삭제
### DELETE /profile/dog?id=${애견 id}

### 응답
```
200_ok   
text: "success delete"  
```


## ○ 케어타입 
- "WALKING"-산책
- "BOARDING"-돌봄
- "GROOMING"-외견 케어
- "PLAYTIME"-놀아주기
- "ETC"-기타



## 요구 등록
### POST /requirement
```
- body
{  
    "dogId": 애견 ID,
    "careType": 케어 타입, 
    "startTime": 케어 시작 시간 , (YYYY-MM-DDTHH:mm:ssZ)
    "endTime": 케어 종료 시간,  
    "careLocation": {
      "x": 경도,
      "y": 위도
    },
    "reward": 보상,
    "description": "설명"  
}
```

### 응답
```
200_ok   
{
   id: 등록 id
}
```

## 내 요구 리스트 조회 
### GET /requirement/list/me?offset=${page no}

### 응답
```
200_ok   
{  
   "requirements":  
   [  
      {     
        "id": 등록 id,
        "image": 애견 이미지,
        "breed": 견종,
        "time": 일자,   
        "careType": 케어 타입, 
        "status": 등록 상태
      },  
      ...  
  ]  
}
```


## 내 요구 조회 
### GET /requirement/me?id=${등록 id}

### 응답
```
200_ok   
{  
  "details": {
    "id": 등록 id,
    "dogImage": 애견 이미지,
    "careType": 케어 타입,
    "careLoaction": {
      "x": 경도,
      "y": 위도
    },
    "description": 설명,
    "userId": 유저 id,
    "dogId": 애견 id,
    "status": 등록 상태,
    "reward": 보상 
  },
  "applications": 
  [  (신청 리스트)  
    {  
      "id": 신청 id,
      "userId": 유저 id, 
      "image": 유저 이미지,
      "name": 유저 이름,
      "gender": 성별,
      "rating": 평균 평점 (0~5)
  },...
  ]  
}  
```

## 요구 리스트 조회 
### GET /requirement/list?offset=${page no}
```
- body
{    
     "location":{  :required)  
        "x": 위도,
        "y": 경도
    },
      "radius": 반경, (기본:5, 최대:10)
      "dogSize": 애견 크기, ("SMALL"-소형, "MEDIUM"-중형, "LARGE"-대형) (영어로 정확히 보낼 것)
      "careType": 케어 타입
  }
```

### 응답
```
200_ok   
{  
   "requirements":  
   [  
      {     
        "id": 등록 id,
        "image": 애견 이미지,
        "breed": 견종,
        "careType": 케어 타입, 
        "time": 일자,  
        "status": 등록 상태
      },  
      ...  
  ]  
}
```

## 요구 조회 
### GET /requirement?id=${등록 id}

### 응답
```
200_ok
 {
    "id": 등록 id,
    "dogImage": 애견 이미지,
    "careType": 케어 타입,
    "careLoaction": {
      "x": 경도,
      "y": 위도
    },
    "description": 설명,
    "userId": 유저 id,
    "dogId": 애견 id,
    "reward": 보상,
    "status": 등록 상태
  }
```

## 요구 취소 
### PATCH /requirement/cancel?id=${등록 id}


### 응답
```
200_ok
text: "success cancel"  
```


## 신청 리스트 조회 
### GET /application/list?offset=${page no}


### 응답
```
200_ok
{  
   "applications":  
   [  
      {     
        "id": 신청 id,
        "image": 애견 이미지,
        "breed": 견종,
        "careType": 케어 타입, 
        "time": 일자,  
        "status": 신청 상태
      },  
      ...  
  ]  
}
```

## 신청 조회 
### GET /application?id=${신청 id}

### 응답
```
200_ok
 {
    "id": 신청 id,
    "dogImage": 애견 이미지,
    "careType": 케어 타입,
    "careLoaction": {
      "x": 경도,
      "y": 위도
    },
    "description": 설명,
    "userId": 요구 등록 유저 id,
    "dogId": 애견 id,
    "reward", 보상,
    "status": 신청 상태
  }
```

## 신청
### POST /application?requrementId=${등록 id}

### 응답
```
200_ok  
{id: 신청 id}
```

## 신청 취소
### PATCH /application/cancel?id=${신청 id}


### 응답
```
200_ok
text: "success cancel"  
```


## 신청 수락
### POST /match?requirementId=${등록 id}&applicationId=${신청 id}

### 응답
```
200_ok  
{id: 매칭 id}
```

## 가장 빠른 매칭 정보 반환 
### GET /match/upcoming

### 응답
```
200_ok
{    
  "id": 매칭 id,  
  "image": 애견 이미지,  
  "breed": 견종,  
  "careType": 케어 타입,   
  "time": 일자,    
  "status": 매칭 상태  
}
```



## 매칭 리스트 조회 
### GET /match/list?offset=${page no}


### 응답
```
200_ok
{  
   "matches":  
   [  
      {  
        "id": 매칭 id,
        "image": 애견 이미지,
        "breed": 견종,
        "careType": 케어 타입, 
        "time": 일자,  
        "status": 매칭 상태
      },  
      ...  
  ]  
}
```


## 매칭 조회 
### GET /match?id=${매칭 id}

### 응답
```
200_ok
{  
  "details": {
    "id": 매칭 id,
    "dogImage": 애견 이미지,
    "careType": 케어 타입,
    "careLoaction": {
      "x": 경도,
      "y": 위도
    },
    "description": 설명,
    "userId": 매칭 상대 유저 id,
    "dogId": 애견 id,
    "reward", 보상,
    "status": 매칭 상태
  },  
  "requester": 결제할 사람인지 여부
}
```

```
▶ "requester" -> true (요구 등록자라는 뜻)  
- status: WAITING_PAYMENT -> 결제 버튼 표시
- status: NOT_COMPLETED -> 완료 버튼 표시
- status: COMPLETED -> 리뷰 버튼 표시  
- status: 그 외 -> 상태 표시 
  

▶ "requester" -> false (요구 지원자라는 뜻)  
- status: COMPLETED -> 리뷰 버튼 표시   
- status: 그 외 -> 상태 표시
```

## 매칭 완료 
### PATCH /match/complete?id=${매칭 id}

### 응답
```
200_ok
text: "success complete"  
```

## 매칭 취소 (= 매칭이 WAITING_PAYMENT 상태일 때 취소 동작)
### PATCH /match/cancel?id=${매칭 id}

### 응답
```
200_ok
text: "success cancel"  
```


## 결제 요청 
### GET /payment/ready?matchId=${매칭 id}

### 응답
```
302_redirection
redriect: kakao 인증 페이지 
```

```
- 인증 성공시 ->
  {  
    "payment cost": 결제 금액 (int),
    "approve time": 승인 시간,
    "payment type": 결제 방법
  }
- 인증 취소시 -> text: "cancel payment"
- 인증 실패시 -> text: "fail payment"
```

## 결제 취소 (= 매칭이 NOT_COMPLETED 상태일 때 취소 동작 
### PATCH /payment/refund?matchId=${매칭 id}

### 응답
```
200_oK
{  
    "refund cost": 환불 금액 (int),
    "approve time": 승인 시간,
    "payment type": 결제 방법
}
```


## 채팅
```
- connection - ws://13.209.220.187/ws (헤더: stompConnectHeaders - Authorization: Bearer ${accessToken value})
- subscribe - /exchange/chat.exchange/*.room.${matchId} (sub 데이터: {"message": ${text}, "sender": ${username}, "createdAt": ${DateTime}}
- send - /send/chat.talk.${matchId} (pub 데이터: ${text})
```


## 채팅 기록 조회
### GET /chat/history?roomId=${매칭 id}

### 응답
```
200_ok
{   
  "messages": [  (message List)        
    {  
      "id": 매칭 id,  
      "sender": 전송자 이름,  
      "createdAt": 전송 시간  
    },
      ...
  ]
}
```
  
## 리뷰 등록
### POST /review  

```
- body
{  
     "id": 매칭 id, (required)   
     "rating": 평점, (0~5)  
     "text": 리뷰 (required)  
  }
```

### 응답
```
200_ok  
{id: 매칭 id}
```

## 리뷰 삭제
### Delete /review?id=${리뷰 id}

### 응답
```
200_ok  
text: "success delete"
```

## 리뷰 조회
### GET /review?matchId=${match Id}  

### 응답
```
200_ok  
{    
     "id": 리뷰 id, (required)   
     "rating": 평점, (float, 0~5)  
     "text": 리뷰 (required)  
}
```

!리뷰가 없을 경우  
```
{  
  "id": null
}
```

이전 매칭 조회 결과 requester 필드에 따른 후속 동작
```
▶ "requester" -> true (요구 등록자라는 뜻)  
- id: null -> 리뷰 작성 페이지
- id: not null -> 리뷰 표시 & 삭제 버튼 활성화 

▶ "requester" -> false (요구 지원자라는 뜻)  
- 항상 리뷰 표시 
```


## 리뷰 리스트 조회
### GET /review/list?userId=${유저 id}&offset=${page no}

### 응답
```
200_ok
{   
  "reviews": [  (review List)     
    {  
      "id": 리뷰 id,    
      "matchId": 매칭 id,    
      "rating": 평점, (0~5)   
      "text": 리뷰,  
      "createdAt": 작성 시간,  
      "breed": 견종,  
      "careType": 케어 타입    
    },
      ...
  ]  
}
```

## fcm 토큰 등록
### POST /fcm/token
```
- body
{   
  "token": ${fcm 토큰 value}  
}
```

### 응답
```
test: success register
```
 

## dummy 유저 token 반환
### GET /user/dummy?id=${유저 id} (범위 1~50)

### 응답
```
text: 해당 유저 accessToken
```


