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
  
  "accessToken": `${accessToken value}`,  
  "refreshToken": `${refreshToken value}`  
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


## 내 프로필 정보
### GET /profile/user/me  

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

## 유저 프로필 정보
### GET /profile/user?id=${유저 Id}

### 응답
200_ok   
{  

    "id": 유저 id,  
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
  Content-Type: image

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


잘못된 ID일 경우   
400_bad request   
{
  
    "error": "bad request",
    "message": "can't find dog",
    "status": 400
}   


## 애견 프로필 리스트 조회
### GET /profile/dog/list

### 응답
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
  Content-Type: image

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

### 응답
200_ok   
text: "success change"   




### ◆ multipart/form-data 입력에서 required 값이 입력이 안됐거나 잘못된 값일 경우 응답
에러 리스트 
[ 

    {
        "message": ${에러 메시지},
        "field": ${잘못된 필드},
        "rejectedValue": ${거부된 값(입력된 값)},
        "code": ${에러 종류}
      }
]

▶ 에러가 났을 경우 거부된 값을 적절한 필드에 채우고 에러메시지와 함께 입력폼 재반환 (현재는 에러 메시지가 유효하지 않음)



## 애견 프로필 삭제
### DELETE /profile/dog?id=${애견 id}

### 응답
200_ok   
text: "success delete"  


잘못된 ID일 경우   
400_bad request   
{
  
    "error": "bad request",
    "message": "can't find dog",
    "status": 400
}   


## 케어타입 (인터페이스는 선택 박스로, api 파라미터는 영어로 정확히 입력할 것)
- "WALKING"-산책
- "BOARDING"-돌봄
- "GROOMING"-외견 케어
- "PLAYTIME"-놀아주기
- "ETC"-기타



## 요구 등록
### POST /requirement
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


### 응답
200_ok   
{
   id: 등록 id
}



## 내 요구 리스트 조회 
### GET /requirement/list/me?offset=${page no}

### 응답
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



## 내 요구 조회 
### GET /requirement/me?id=${등록 id}

### 응답
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


## 요구 리스트 조회 
### GET /requirement/list?offset=${page no}
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

### 응답
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


## 요구 조회 
### GET /requirement?id=${등록 id}

### 응답
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


## 요구 취소 
### PUT /requirement/cancel?id=${등록 id}


### 응답
200_ok
text: "success cancel"  



## 신청 리스트 조회 
### GET /application/list?offset=${page no}


### 응답
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


## 신청 조회 
### GET /application?id=${신청 id}

### 응답
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


## 신청
### POST /application?requrementId=${등록 id}

### 응답
200_ok  
{id: 신청 id}


## 신청 취소
### GET /application/cancel?id=${신청 id}


### 응답
200_ok
text: "success cancel"  



## 신청 수락
### POST /match?requirementId=${등록 id}&applicationId=${신청 id}

### 응답
200_ok  
{id: 매칭 id}



## 매칭 리스트 조회 
### GET /match/list?offset=${page no}


### 응답
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



## 매칭 조회 
### GET /match?id=${매칭 id}

### 응답
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

▶ "requester" -> true (요구 등록자라는 뜻)  
- status: WAITING_PAYMENT -> 결제 버튼 표시
- status: NOT_COMPLETED -> 완료 버튼 표시
- status: COMPLETED -> 리뷰 버튼 표시  
- status: 그 외 -> 상태 표시 
  

▶ "requester" -> false (요구 지원자라는 뜻)  
- status: COMPLETED -> 리뷰 버튼 표시   
- status: 그 외 -> 상태 표시


## 매칭 완료 
### GET /match/complete?id=${매칭 id}

### 응답
200_ok
text: "success complete"  


## 매칭 취소 (= 매칭이 WAITING_PAYMENT 상태일 때 취소 동작)
### GET /match/cancel?id=${매칭 id}

### 응답
200_ok
text: "success cancel"  



## 결제 요청 
### GET /payment/ready?matchId=${매칭 id}

### 응답
302_redirection
redriect: kakao 인증 페이지 

- 인증 성공시 -> text: "success payment total: 결제금액"  
- 인증 취소시 -> text: "cancel payment"
- 인증 실패시 -> text: "fail payment"


## 결제 취소 (= 매칭이 NOT_COMPLETED 상태일 때 취소 동작 
### GET /payment/refund?matchId=${매칭 id}

### 응답
200_oK
test: "refund payment total: 환불 금액"



## 채팅
- connection - ws://13.209.220.187/ws (헤더: stompConnectHeaders - Authorization: Bearer ${accessToken value})
- subscribe - /exchange/chat.exchange/*.room.${matchId} (sub 데이터: {"message": ${text}, "sender": ${username}, "createdAt": ${DateTime}}
- send - /send/chat.talk.${matchId} (pub 데이터: ${text})



## 채팅 기록 조회
### GET /chat/history?roomId=${매칭 id}

### 응답
200_ok
{   
  "messages": [  (message List)  
      
    {  
      "id": 매칭 id,  
      "dogImage": 애견 이미지,  
      "careType": 케어 타입  
    },
      ...
  ]

}

  
## 리뷰 등록
### POST /review  

- body

{  
     
     "id": 매칭 id, (required)   
     "rating": 평점, (0~5)  
     "text": 리뷰 (required)  
  }

### 응답
200_ok  
{id: 매칭 id}


## 리뷰 삭제
### Delete /review?id=${리뷰 id}

### 응답
200_ok  
text: "success delete"


## 리뷰 조회
### GET /review?matchId=${match Id}  

### 응답
200_ok  
{  
     
     "id": 리뷰 id, (required)   
     "rating": 평점, (0~5)  
     "text": 리뷰 (required)  
}

!리뷰가 없을 경우  
{  
  "id": null
}

이전 매칭 조회 결과 requester 필드에 따른 후속 동작
▶ "requester" -> true (요구 등록자라는 뜻)  
- id: null -> 리뷰 작성 페이지
- id: not null -> 리뷰 표시 & 삭제 버튼 활성화 

▶ "requester" -> false (요구 지원자라는 뜻)  
- 항상 리뷰 표시 



## 리뷰 리스트 조회
### GET /review/list?userId=${유저 id}&offset=${page no}

### 응답
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

## fcm 토큰 등록
### POST /fcm/token

- body

{   
  "description": ${fcm 토큰 value}  (key 값은 추후 token으로 변경 예정, 현재는 description)
}

### 응답
test: success register
   
!FCM 토큰이 등록되지 않았을 경우 특정 api에서 문제 발생   
!매 로그인 마다 값을 받아 등록할 것 
kk


## dummy 유저 token 반환
### GET /user/dummy?id=${유저 id} (범위 1~50)

### 응답
text: 해당 유저 accessToken


!참고 
유저 수: 1~50   
유저 당 애견 수: 4 (총 200)  
유저 당 등록 수: 4 (총 200)  
유저 당 신청 수: 4 (총 200)  
유저 당 매칭 수: 1 (총 50)  

모든 등록 위치는 금오공대 중심으로 4~10km 범위  

금오공대 위치 좌표   
{  
    
  "x": 128.3936,  
  "y": 36.1461  
}


## 가장 빠른 매칭 정보 반환 
### GET /match/upcoming

### 응답
{    
    
  "id": 매칭 id,  
  "image": 애견 이미지,  
  "breed": 견종,  
  "careType": 케어 타입,   
  "time": 일자,    
  "status": 매칭 상태  
  
}

