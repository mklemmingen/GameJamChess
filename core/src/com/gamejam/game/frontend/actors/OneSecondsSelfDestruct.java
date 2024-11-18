package com.gamejam.game.frontend.actors;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class OneSecondsSelfDestruct extends Actor {
    /*
    This class takes an Image, a Table or a Stack and adds it as its Image.
    This class self destructs 3 seconds after its first time being acted on (not drawn!)
     */

    Stack selfImage = new Stack();
    float timeElapsed = 0;

    public OneSecondsSelfDestruct(Image image, float x, float y){
        selfImage.add(image);
        sizeMe(image.getHeight(), image.getWidth());
        pos(x, y);
    }
    public OneSecondsSelfDestruct(Table table, float x, float y){
        selfImage.add(table);
        sizeMe(table.getHeight(), table.getWidth());
        pos(x, y);
    }
    public OneSecondsSelfDestruct(Stack stack, float x, float y){
        selfImage.add(stack);
        sizeMe(stack.getHeight(), stack.getWidth());
        pos(x, y);
    }

    private void sizeMe(float height, float width){
        selfImage.setSize(width, height);
    }

    private void pos(float x, float y){
        x -= selfImage.getWidth() / 2;
        y -= selfImage.getHeight() / 2;
        selfImage.setPosition(x, y);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        selfImage.draw(batch, parentAlpha);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        timeElapsed += delta;
        if (timeElapsed > 1){
            clear();
            selfImage.remove();
            remove();
        }
    }

    public void setSelfVisage(Stack stack){
        selfImage = stack;
    }

    public Stack getSelfVisage(){
        return selfImage;
    }
}
