package com.st001.test

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.MathUtils
import kotlin.math.sin

class CavedwellerCharacter {
    // Các trạng thái của nhân vật
    enum class State { IDLE, WALK, ATTACK }
    var currentState = State.IDLE
        private set

    // Sprites lưu trữ các bộ phận (Dựa theo ảnh bóc tách của bạn)
    private lateinit var body: Sprite
    private lateinit var head: Sprite
    private lateinit var armLeft: Sprite
    private lateinit var armRightWeapon: Sprite
    private lateinit var thighLeft: Sprite
    private lateinit var shinLeft: Sprite
    private lateinit var thighRight: Sprite
    private lateinit var shinRight: Sprite

    // Các Sprite cũ giữ nguyên...
// === THÊM CÁC BỘ PHẬN MỚI VÀO ĐÂY ===
    private lateinit var armRight: Sprite       // Cẳng tay phải (nếu bạn muốn tách rời gậy)
    private lateinit var handLeft: Sprite       // Bàn tay trái
    private lateinit var footLeft: Sprite       // Bàn chân trái
    private lateinit var footRight: Sprite      // Bàn chân phải

    // Các biến quản lý thời gian animation
    private var stateTime = 0f
    private var attackTime = 0f
    private val attackDuration = 0.4f // Thời gian diễn ra 1 cú chém (0.4 giây)

    fun create() {
        // 1. Nạp các mảnh ảnh (Hãy đảm bảo tên file trùng khớp với file trong assets)
        body = Sprite(Texture("body.png"))
        head = Sprite(Texture("head.png"))
        armLeft = Sprite(Texture("arm_left.png"))
        armRightWeapon = Sprite(Texture("arm_right_weapon.png")) // Cánh tay cầm gậy bên phải
        thighLeft = Sprite(Texture("thigh_left.png"))
        shinLeft = Sprite(Texture("shin_left.png"))
        thighRight = Sprite(Texture("thigh_right.png"))
        shinRight = Sprite(Texture("shin_right.png"))
        armRight = Sprite(Texture("arm_right.png"))
        handLeft = Sprite(Texture("hand_left.png"))
        footLeft = Sprite(Texture("foot_left.png"))
        footRight = Sprite(Texture("foot_right.png"))

        // 2. THIẾT LẬP TÂM XOAY (ORIGIN) CHO CÁC KHỚP XƯƠNG (Tính bằng pixel từ góc dưới-trái)
        // Bạn hãy tinh chỉnh các thông số này để khớp xoay không bị lệch khi chuyển động
        body.setOrigin(body.width / 2f, body.height / 2f)
        head.setOrigin(head.width / 2f, 5f)

        handLeft.setOrigin(handLeft.width / 2f, handLeft.height - 5f)

        armLeft.setOrigin(armLeft.width - 5f, armLeft.height - 5f) // Vai trái
        armRightWeapon.setOrigin(5f, armRightWeapon.height - 10f)  // Vai phải (Cầm gậy)

        armRight.setOrigin(5f, armRight.height - 5f)

        thighLeft.setOrigin(thighLeft.width / 2f, thighLeft.height - 5f)   // Hông trái
        shinLeft.setOrigin(shinLeft.width / 2f, shinLeft.height - 5f)      // Đầu gối trái

        thighRight.setOrigin(thighRight.width / 2f, thighRight.height - 5f) // Hông phải
        shinRight.setOrigin(shinRight.width / 2f, shinRight.height - 5f)   // Đầu gối phải

        footLeft.setOrigin(footLeft.width / 3f, footLeft.height - 3f)
        footRight.setOrigin(footRight.width / 3f, footRight.height - 3f)
    }

    fun setCharacterState(state: State) {
        if (currentState == State.ATTACK && state != State.IDLE) return // Đang chém thì không ngắt nửa chừng
        if (currentState != state) {
            currentState = state
            if (state != State.ATTACK) stateTime = 0f // Reset thời gian để animation chạy mượt từ đầu
        }
    }

    fun startAttack() {
        currentState = State.ATTACK
        attackTime = 0f
    }

    fun update(deltaTime: Float) {
        stateTime += deltaTime

        // Biến lưu trữ góc xoay và vị trí tạm thời của các khớp
        var bodyOffsetY = 0f
        var headRot = 0f
        var armLRot = 0f
        var armRRot = 0f
        var thighLRot = 0f
        var shinLRot = 0f
        var thighRRot = 0f
        var shinRRot = 0f

        when (currentState) {
            State.IDLE -> {
                // --- ANIMATION THỞ (IDLE) ---
                bodyOffsetY = sin(stateTime * 3.5f) * 2.5f // Thân nhấp nhô nhẹ 2.5 pixel
                headRot = sin(stateTime * 3.5f) * 2f        // Đầu gật nhẹ 2 độ nghịch pha với thân
                armLRot = sin(stateTime * 2f) * 5f          // Tay đung đưa nhẹ
                armRRot = -sin(stateTime * 2f) * 4f
            }
            State.WALK -> {
                // --- ANIMATION ĐI BỘ (WALK) ---
                bodyOffsetY = Math.abs(sin(stateTime * 10f)) * 4f // Đi bộ thì người nhấp nhô nhanh hơn
                headRot = sin(stateTime * 10f) * 3f
                armLRot = sin(stateTime * 8f) * 30f              // Hai tay đánh sải cánh rộng
                armRRot = -sin(stateTime * 8f) * 25f

                // Chân trái và chân phải xoay nghịch pha nhau ($180^\circ$) để bước đi
                val walkCycle = stateTime * 8f
                thighLRot = sin(walkCycle) * 25f
                shinLRot = Math.max(0f, sin(walkCycle + MathUtils.PI / 4f) * 20f) // Gập đầu gối khi nhấc chân

                thighRRot = -sin(walkCycle) * 25f
                shinRRot = Math.max(0f, -sin(walkCycle + MathUtils.PI / 4f) * 20f)
            }
            State.ATTACK -> {
                attackTime += deltaTime
                val progress = attackTime / attackDuration

                if (progress >= 1f) {
                    currentState = State.IDLE // Chém xong quay về trạng thái đứng yên
                    attackTime = 0f
                } else {
                    // --- ANIMATION CHÉM VŨ KHÍ (ATTACK) ---
                    // Giai đoạn 1 (0 -> 30% thời gian): Giơ gậy lên lấy đà
                    if (progress < 0.3f) {
                        val t = progress / 0.3f
                        // ĐỔI TỪ .interpolate THÀNH .apply
                        armRRot = Interpolation.linear.apply(0f, -45f, t)
                        bodyOffsetY = Interpolation.linear.apply(0f, -3f, t)
                    }
                    // Giai đoạn 2 (30% -> 70% thời gian): Vụt mạnh gậy xuống
                    else if (progress < 0.7f) {
                        val t = (progress - 0.3f) / 0.4f
                        // ĐỔI TỪ .interpolate THÀNH .apply
                        armRRot = Interpolation.swingOut.apply(-45f, 75f, t)
                        bodyOffsetY = Interpolation.linear.apply(-3f, 2f, t)
                    }
                    // Giai đoạn 3 (70% -> 100% thời gian): Thu gậy về vị trí cũ chậm rãi
                    else {
                        val t = (progress - 0.7f) / 0.3f
                        // ĐỔI TỪ .interpolate THÀNH .apply
                        armRRot = Interpolation.linear.apply(75f, 0f, t)
                        bodyOffsetY = Interpolation.linear.apply(2f, 0f, t)
                    }
                }
            }
        }

        // 3. ÁP DỤNG MA TRẬN PHỤ THUỘC TỌA ĐỘ (PARENT - CHILD HIERARCHY)
        val rootX = 400f
        val rootY = 250f

        // Đặt vị trí các bộ phận cũ (Body, Head, ArmLeft, Thigh, Shin...)
        body.setPosition(rootX, rootY + bodyOffsetY)
        // ...

        // ================= CẬP NHẬT TỌA ĐỘ BỘ PHẬN MỚI =================

        // 1. BÀN TAY TRÁI (Ăn theo Tay trái - armLeft)
        // Bàn tay trái nằm ở điểm cuối của Tay trái. Góc xoay sẽ cộng dồn góc của Tay trái.
        handLeft.setPosition(armLeft.x + 5f, armLeft.y - handLeft.height + 5f)
        handLeft.rotation = armLeft.rotation // Tự động xoay theo cánh tay khi đánh sải

        // 2. CẲNG TAY PHẢI (Ăn theo Thân - đối xứng với tay trái)
        armRight.setPosition(body.x + body.width - 15f, body.y + body.height - 40f)
        armRight.rotation = armRRot // Chạy theo góc xoay của hành động thở/chém

        // 3. BÀN CHÂN TRÁI (Ăn theo Cẳng chân trái - shinLeft)
        // Bàn chân nằm ở dưới đáy của cẳng chân trái.
        footLeft.setPosition(shinLeft.x - 5f, shinLeft.y - footLeft.height + 3f)
        // Góc xoay bằng góc của đùi + cẳng chân để khi gập gối, bàn chân vẫn hướng đúng trục
        footLeft.rotation = thighLeft.rotation + shinLeft.rotation

        // 4. BÀN CHÂN PHẢI (Ăn theo Cẳng chân phải - shinRight)
        footRight.setPosition(shinRight.x - 5f, shinRight.y - footRight.height + 3f)
        footRight.rotation = thighRight.rotation + shinRight.rotation
    }

    fun draw(batch: SpriteBatch) {
        handLeft.draw(batch)        // Vẽ bàn tay trái ở lớp sau cùng
        armLeft.draw(batch)

        footLeft.draw(batch)        // Vẽ hai bàn chân trước rồi mới vẽ cẳng chân đè lên
        footRight.draw(batch)

        shinLeft.draw(batch)
        thighLeft.draw(batch)
        shinRight.draw(batch)
        thighRight.draw(batch)

        body.draw(batch)
        head.draw(batch)

        armRight.draw(batch)        // Vẽ tay phải ở lớp tiền cảnh ngoài cùng
        armRightWeapon.draw(batch)  // Nếu bạn tách riêng gậy, vẽ gậy sau cùng đè lên tay phải
    }

    // Giải phóng
    fun dispose() {
        body.texture.dispose()
        head.texture.dispose()
        armLeft.texture.dispose()
        armRightWeapon.texture.dispose()
        thighLeft.texture.dispose()
        shinLeft.texture.dispose()
        thighRight.texture.dispose()
        shinRight.texture.dispose()
        armRight.texture.dispose()
        handLeft.texture.dispose()
        footLeft.texture.dispose()
        footRight.texture.dispose()
    }
}
