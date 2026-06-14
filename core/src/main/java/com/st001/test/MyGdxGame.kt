package com.st001.test

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch

class MyGdxGame : ApplicationAdapter() {

    private lateinit var character1: CavedwellerCharacter
    private lateinit var character2: CavedwellerCharacter

    override fun create() {

        character1 = CavedwellerCharacter()
        character1.create()

        character2 = CavedwellerCharacter()
        character2.create()

        character1.setPosition(
            Gdx.graphics.width * 0.25f,
            100f
        )

        character2.setPosition(
            Gdx.graphics.width * 0.75f,
            100f
        )

        character2.faceLeft()
    }

    override fun render() {

        Gdx.gl.glClearColor(0.92f, 0.92f, 0.92f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        val dt = Gdx.graphics.deltaTime

        handleInput()

        character1.update(dt)
        character2.update(dt)

        character1.render()
        character2.render()
    }

    private fun handleInput() {

        val speed = 300f * Gdx.graphics.deltaTime

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            character1.move(-speed, 0f)
            character1.faceLeft()
        }

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            character1.move(speed, 0f)
            character1.faceRight()
        }

        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            character1.move(0f, speed)
        }

        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            character1.move(0f, -speed)
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            character1.startAttack()
        }

        if (Gdx.input.justTouched()) {
            character1.startAttack()
            character2.startAttack()
        }

        if (character1.getX() < character2.getX()) {
            character1.faceRight()
            character2.faceLeft()
        } else {
            character1.faceLeft()
            character2.faceRight()
        }
    }



    override fun dispose() {
        character1.dispose()
        character2.dispose()
    }
}
