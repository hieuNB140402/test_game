package com.st001.test

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch

class MyGdxGame : ApplicationAdapter() {
    private lateinit var batch: SpriteBatch
    private lateinit var character: CavedwellerCharacter

    override fun create() {
        batch = SpriteBatch()

        // Khởi tạo thực thể nhân vật hoạt họa xích ma
        character = CavedwellerCharacter()
        character.create()
    }

    override fun render() {
        // Xóa sạch khung hình cũ với màu xám nhạt làm nền
        Gdx.gl.glClearColor(0.92f, 0.92f, 0.92f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        val dt = Gdx.graphics.deltaTime

        // LẮNG NGHE ĐIỀU KHIỂN TỪ NGƯỜI CHƠI (Tương tác bàn phím hoặc chạm)
        handleInput()

        // Cập nhật logic toán học xương khớp cho nhân vật
        character.update(dt)

        // Tiến hành render vẽ nhân vật lên GPU
        batch.begin()
        character.draw(batch)
        batch.end()
    }

    private fun handleInput() {
        // --- ĐIỀU KHIỂN TRÊN DESKTOP (BÀN PHÍM) ---
        if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT) ||
            Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            character.setCharacterState(CavedwellerCharacter.State.WALK)
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            character.startAttack()
        } else {
            character.setCharacterState(CavedwellerCharacter.State.IDLE)
        }

        // --- ĐIỀU KHIỂN TRÊN ĐIỆN THOẠI (CẢM ỨNG) ---
        // Nếu chạm vào nửa bên phải màn hình thì CHÉM, chạm nửa bên trái thì ĐI BỘ
        if (Gdx.input.isTouched) {
            val touchX = Gdx.input.x.toFloat()
            val screenWidth = Gdx.graphics.width.toFloat()

            if (touchX > screenWidth / 2f) {
                character.startAttack()
            } else {
                character.setCharacterState(CavedwellerCharacter.State.WALK)
            }
        }
    }

    override fun dispose() {
        batch.dispose()
        character.dispose() // Giải phóng toàn bộ mảng vùng nhớ texture của nhân vật
    }
}
