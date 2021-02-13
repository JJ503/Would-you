package com.example.guru2_contestapp

// 개인페이지 > 신청목록 탭의 리사이클러뷰에 사용되는 클래스
class ApplyTeamItem (val t_num : Int,
                     val c_photo : Int,
                     val t_name :String, val c_name :String,
                     val t_now_num :Int, val  t_total_num :Int,
                     val t_end_date  : String,
                     val t_need_part : String,
                     val state : Int
)
