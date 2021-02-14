package com.example.guru2_contestapp

// 개인페이지 > 신청목록 탭의 리사이클러뷰에 사용되는 클래스
class ApplyTeamItem (val t_num : Int, // 팀 번호
                     val c_photo : Int, // 공모전 사진
                     val t_name :String, val c_name :String, // 팀 이름,공모전 이름
                     val t_now_num :Int, val  t_total_num :Int, //현재 팀원 명수 , 모집할 총 팀원 수
                     val t_end_date  : String, // 팀 마감 날짜
                     val t_need_part : String, // 모집 분야
                     val state : Int // 상태 (합격/승인/미정)
)
