package com.example.guru2_contestapp

class ApplicantPagerItem(val t_num : Int,         // 팀 번호
                         val t_endStatus : Int,   // 팀 상태 (모집 종료(0) or 모집 중(1))
                         val m_id : String,       // 사용자 id
                         val m_name : String,     // 사용자 이름
                         val m_age : Int,         // 사용자 나이
                         val r_hope : String,
                         val m_profile : String) {  // 사용자 희망 분야
}