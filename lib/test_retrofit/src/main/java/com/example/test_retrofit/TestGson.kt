package com.example.test_retrofit

import com.example.test_retrofit.net.ApiException
import com.example.test_retrofit.net.BaseRsp
import com.example.test_retrofit.net.ResponseStatusInterceptor
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

fun main() {
    val json =
        "{\"msg\":\"\",\"data\":{\"id\":3,\"primeLectureId\":20,\"primeChallenge\":{\"id\":1,\"title\":\"0814测试\",\"ruleDesc\":\"1、活动期间，用户可以通过行者App获取相关赛事信息，竞猜结果和积分信息则可以通过行者账户系统进行查看。\\r\\n\\r\\n2、此次【环意“峰”情节】竞猜活动面向全部用户，非行者用户可以通过微信登录参与活动；获得相关奖励之后，需下载行者App并注册完善相关用户资料，方可进行兑换；行者用户使用微信登录或者进行绑定微信操作之后，该账户则视为唯一竞猜账户，不得进行更改。\\r\\n\\r\\n3、此次【环意“峰”情节】竞猜活动将使用行者运动积分兑换竞猜资格，竞猜结果正确将按照既定赔率奖励相应运动积分，竞猜结果错误将不返还所使用积分。\\r\\n\\r\\n4、运动积分只能通过使用行者进行骑行、跑步、徒步以及相应行者奖励规则获得，其他渠道获取的积分视为无效，将不能参与竞猜活动，积分发放规则解释权归行者所有。\\r\\n\\r\\n5、参与竞猜时间：各项竞猜将在活动上线后立即开启，每个竞猜项目均有相应的关闭时间，具体时间请至竞猜页查看，请注意各竞猜关闭时间，及时参与竞猜。\\r\\n\\r\\n6、竞猜积分结算时间：分赛段冠军竞猜结果将在该比赛日结束后到下一个比赛日开始之前期间进行结算；其余项目将在最后一个赛段结束后一个工作日内进行结算；结算规则将依据用户使用积分参与竞猜时的赔率进行；赔率信息来自于国外专业网站。\\r\\n\\r\\n7、活动结束当积分出现并列情况时，将首先根据用户参与竞猜的次数决定排名，次数大于十五次的获得优先；如果用户竞猜次数同大于或者小于十五次，则根据用户胜率决定排名，胜率高的获得优先。\\r\\n\\r\\n8、净收积分排行榜将会在活动页面展示，行者将会在活动结束后公布获奖名单，并联系获奖用户发放奖品。\\r\\n\\r\\n9、赔率信息来自于国外专业网站，行者不对其做任何形式的背书。\\r\\n\\r\\n10、此次【环意“峰”情节】竞猜活动最终解释权归上海大不自多科技信息有限公司所有。\"},\"finishedCount\":0,\"status\":1,\"exercise\":{\"tikuCourseId\":1,\"tikuPrefix\":\"xingce\",\"tikuExerciseId\":322391147,\"sheetSource\":2},\"userRank\":{\"user\":{\"id\":203529282,\"nickName\":\"0027\",\"avatarUrl\":\"http://userprofile.fbstatic.cn/203529282-1597303976978.jpg?x-oss-process=style%2Fbig\",\"memberInfo\":null,\"createdTime\":1595410258866,\"encodedAccount\":\"176****0027\"},\"rank\":8,\"finishRatio\":0.65,\"studyTime\":480000},\"ranks\":[{\"user\":{\"id\":103519442,\"nickName\":\"风息\",\"avatarUrl\":\"https://fb.fbstatic.cn/api/ape-images/1610d635611e0d9.png\",\"memberInfo\":null,\"createdTime\":1497327851351,\"encodedAccount\":\"test****@fenbi.com\"},\"rank\":1,\"finishRatio\":1,\"studyTime\":60000},{\"user\":{\"id\":203528743,\"nickName\":\"18742026978\",\"avatarUrl\":\"https://fb.fbstatic.cn/api/ape-images/1610d635611e0d9.png\",\"memberInfo\":null,\"createdTime\":1590394210925,\"encodedAccount\":\"187****6978\"},\"rank\":2,\"finishRatio\":0.95,\"studyTime\":120000},{\"user\":{\"id\":15999523,\"nickName\":\"11-9哈哈哈\",\"avatarUrl\":\"https://fb.fbstatic.cn/api/ape-images/1610d635611e0d9.png\",\"memberInfo\":null,\"createdTime\":1429612775049,\"encodedAccount\":\"11****@9.com\"},\"rank\":3,\"finishRatio\":0.9,\"studyTime\":180000},{\"user\":{\"id\":15999521,\"nickName\":\"1_9\",\"avatarUrl\":\"http://userprofile.fbstatic.cn/15999521-1536132830408.jpg?x-oss-process=style%2Fbig\",\"memberInfo\":null,\"createdTime\":1429612717487,\"encodedAccount\":\"1****@9.com\"},\"rank\":4,\"finishRatio\":0.85,\"studyTime\":240000},{\"user\":{\"id\":103525632,\"nickName\":\"hanz2Z\",\"avatarUrl\":\"http://userprofile.fbstatic.cn/103525632-1573616801263.jpg?x-oss-process=style%2Fbig\",\"memberInfo\":null,\"createdTime\":1537868099215,\"encodedAccount\":\"159****5106\"},\"rank\":5,\"finishRatio\":0.8,\"studyTime\":300000},{\"user\":{\"id\":103519542,\"nickName\":\"哈哈哈\",\"avatarUrl\":\"https://fb.fbstatic.cn/api/ape-images/1610d635611e0d9.png\",\"memberInfo\":null,\"createdTime\":1501231316354,\"encodedAccount\":\"186****9852\"},\"rank\":6,\"finishRatio\":0.75,\"studyTime\":360000},{\"user\":{\"id\":103519436,\"nickName\":\"黛西\",\"avatarUrl\":\"https://fb.fbstatic.cn/api/ape-images/1610d635611e0d9.png\",\"memberInfo\":null,\"createdTime\":1497327823722,\"encodedAccount\":\"test****@fenbi.com\"},\"rank\":7,\"finishRatio\":0.7,\"studyTime\":420000},{\"user\":{\"id\":203529282,\"nickName\":\"0027\",\"avatarUrl\":\"http://userprofile.fbstatic.cn/203529282-1597303976978.jpg?x-oss-process=style%2Fbig\",\"memberInfo\":null,\"createdTime\":1595410258866,\"encodedAccount\":\"176****0027\"},\"rank\":8,\"finishRatio\":0.65,\"studyTime\":480000}]}}"

    val gson = Gson();

    val status: BaseRsp<Void> = gson.fromJson<BaseRsp<Void>>(
        json,
        object : TypeToken<BaseRsp<Void?>?>() {}.type
    )

    if ((status.code != 1)) {
        throw ApiException(status.code, (if (status.msg == null) "" else status.msg!!))
    }
}