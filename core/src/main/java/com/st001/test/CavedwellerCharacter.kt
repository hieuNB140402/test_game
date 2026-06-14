package com.st001.test

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.esotericsoftware.spine.*
import com.esotericsoftware.spine.utils.TwoColorPolygonBatch

class CavedwellerCharacter {

    private lateinit var atlas: TextureAtlas

    private lateinit var skeleton: Skeleton

    private lateinit var animationState: AnimationState

    private lateinit var renderer: SkeletonRenderer

    private lateinit var batch: TwoColorPolygonBatch

    private var posX = 0f
    private var posY = 0f

    fun create() {

        renderer = SkeletonRenderer()
        renderer.premultipliedAlpha = true

        batch = TwoColorPolygonBatch()

        atlas = TextureAtlas(
            Gdx.files.internal("skeleton.atlas")
        )

        val json = SkeletonJson(atlas)
        json.scale = 1f

        val skeletonData =
            json.readSkeletonData(
                Gdx.files.internal("skeleton.json")
            )

        skeleton = Skeleton(skeletonData)
        skeleton.setSkin("skin-2")

        posX = Gdx.graphics.width / 2f
        posY = 100f

        skeleton.setPosition(posX, posY)

        val stateData =
            AnimationStateData(skeletonData)

        stateData.setMix(
            "IDLE",
            "ATTACK",
            0.15f
        )

        stateData.setMix(
            "ATTACK",
            "IDLE",
            0.15f
        )

        animationState =
            AnimationState(stateData)

        animationState.setAnimation(
            0,
            "IDLE",
            true
        )
    }

    fun setPosition(x: Float, y: Float) {
        posX = x
        posY = y

        skeleton.setPosition(
            posX,
            posY
        )
    }

    fun getX(): Float = posX


    fun startAttack() {

        val current =
            animationState.getCurrent(0)
                ?.animation
                ?.name

        if (current == "ATTACK") {
            return
        }

        animationState.setAnimation(
            0,
            "ATTACK",
            false
        )

        animationState.addAnimation(
            0,
            "IDLE",
            true,
            0f
        )
    }

    fun move(dx: Float, dy: Float) {
        posX += dx
        posY += dy
    }

    fun faceLeft() {
        skeleton.scaleX = -1f
    }

    fun faceRight() {
        skeleton.scaleX = 1f
    }

    fun update(delta: Float) {

        skeleton.setPosition(
            posX,
            posY
        )

        animationState.update(delta)

        animationState.apply(skeleton)

        skeleton.updateWorldTransform()
    }

    fun render() {

        batch.begin()

        renderer.draw(
            batch,
            skeleton
        )

        batch.end()
    }

    fun changeSkin(name: String) {

        skeleton.setSkin(name)

        skeleton.setSlotsToSetupPose()
    }

    fun rotateHead(angle: Float) {

        skeleton.findBone("Head")
            ?.rotation = angle
    }

    fun dispose() {

        batch.dispose()

        atlas.dispose()
    }
}
