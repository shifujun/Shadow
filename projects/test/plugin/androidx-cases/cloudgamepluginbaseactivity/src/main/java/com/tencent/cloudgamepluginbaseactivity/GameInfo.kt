/**
 * GameInfo.kt
 * @author blinkjiang
 * @date 2019-07-24
 */
package com.tencent.cloudgamepluginbaseactivity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "game_info")
data class GameInfo(
    @PrimaryKey
    @ColumnInfo(name = "game_id") // 游戏ID
    val gameId: String,

    @ColumnInfo(name = "type") // 游戏类型(普通/wegame/START)
    val type: Int,

    @ColumnInfo(name = "login_type") // 游戏类型(普通/wegame/START)
    val loginType: Int,

    @ColumnInfo(name = "service_id") // service_id(打通登录的游戏请求区分列表时使用)
    val serviceId: String,

    @ColumnInfo(name = "game_version") // game_version(打通登录的游戏请求区分列表时使用)
    val gameVersion: String,

    @ColumnInfo(name = "name") // 游戏名称
    val name: String,

    @ColumnInfo(name = "image") // 已弃用字段
    val image: String,

    @ColumnInfo(name = "icon") // 游戏列表图标url
    val iconImage: String,

    @ColumnInfo(name = "timestamp") // 修改时间戳
    val timestamp: Long,

    @ColumnInfo(name = "image_portrait") // 游戏图标url(纵向)
    val imagePortrait: String,

    @ColumnInfo(name = "image_landscape") // 游戏图标url(横向)
    val imageLandscape: String,

    @ColumnInfo(name = "image_home") // 主界面游戏背景url
    val imageHome: String,

    @ColumnInfo(name = "image_banner") // 游戏详情页banner背景url(可能有多张 逗号分隔)
    val imageBanner: String,

    @ColumnInfo(name = "image_launch") // 游戏加载背景url
    val imageLaunch: String,

    @ColumnInfo(name = "publish_time") // 游戏发行时间
    val publishTime: Long,

    @ColumnInfo(name = "develop_company") // 游戏开发商
    val developCompany: String,

    @ColumnInfo(name = "operator_company") // 游戏运营商
    val operatorCompany: String,

    @ColumnInfo(name = "category") // 游戏类别(如FPS/RTS/RPG)
    val category: String,

    @ColumnInfo(name = "description") // 游戏描述
    val description: String,

    @ColumnInfo(name = "max_controller_count") // 游戏支持的最大手柄数量
    val maxControllerCount: Int,

    @ColumnInfo(name = "extension_type") // 这个项的扩展类型(扩展项目的属性)
    val extensionType: String,

    @ColumnInfo(name = "user_login_type")
    val userLoginType: Int,

    @ColumnInfo(name = "skip_payment")  // 是否可以忽略VIP以及游戏时长
    val skipPayment: Int,

    @ColumnInfo(name = "show_tag_bit")  // 按位表示标签展示，0x1-"测试"，0x2-"限免"，0x4-"热门"
    val showTagBit: Int,

    @ColumnInfo(name = "free_tag_start_ts")  // 开始时间戳，毫秒，0-即刻开始
    val freeTagStartTs: Long,

    @ColumnInfo(name = "free_tag_end_ts")  // 结束时间戳，毫秒，0-永久
    val freeTagEndTs: Long,

    // ${image_base_url} + "/${game_id}/45x45_" +${icon_name}
    @ColumnInfo(name = "image_base_url")
    val imageBaseUrl: String,       //   图片基址

    @ColumnInfo(name = "icon_set")
    val iconSet: String,     //   图片名称集合

    @ColumnInfo(name = "subscribe_mode")
    val subscribeMode:Int, // 2：拷贝制游戏，1：VIP/计时制游戏
    
    @ColumnInfo(name = "play_time_left")
    val playTimeLeft: Long, // 动态信息：剩余游戏时长，没有登录默认60 单位：m

    @ColumnInfo(name = "is_my_game")
    val isMyGame: Int // 动态信息：是否拥有该游戏，0:未拥有该游戏，1:已拥有该游戏
)
