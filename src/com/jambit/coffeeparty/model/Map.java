package com.jambit.coffeeparty.model;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import android.util.Log;

public final class Map {
    
    private final List<Field> board;
    
    private final String boardImage;
    
    private final int fieldIconOffsetX;
    private final int fieldIconOffsetY;
    
    private Map(String imageURL, List<Field> board, int iconOffsetX, int iconOffsetY){
        this.boardImage = imageURL;
        this.board = board;
        this.fieldIconOffsetX = iconOffsetX;
        this.fieldIconOffsetY = iconOffsetY;
    }
    
    public static Map loadFromXML(InputStream xml) throws XPathExpressionException{
        String imageUrl = "";
        int fieldIconOffsetX = 0;
        int fieldIconOffsetY = 0;
        List<Field> newBoard = new ArrayList<Field>();
        List<String> icons = new ArrayList<String>();
        
        XPath xpath = XPathFactory.newInstance().newXPath();
        Node root = (Node) xpath.evaluate("/", new InputSource(xml), XPathConstants.NODE);

        String imageExpr = "/map/boardImage/text()";
        imageUrl = xpath.evaluate(imageExpr, root);
        Log.d("MAP_XML", "background image " + imageUrl);
        
        String xOffsetExpr = "/map/fieldIconOffsetX/text()";
        fieldIconOffsetX = Integer.parseInt(xpath.evaluate(xOffsetExpr, root));
        
        String yOffsetExpr = "/map/fieldIconOffsetY/text()";
        fieldIconOffsetY = Integer.parseInt(xpath.evaluate(yOffsetExpr, root));
        
        String fieldsExpr = "/map/fields/field";
        NodeList fields = (NodeList) xpath.evaluate(fieldsExpr, root, XPathConstants.NODESET);
        for(int i = 0; i < fields.getLength(); i++){
            MinigameIdentifier type = null;
            int x = 0, y = 0;
            String icon = "";
            Node fieldNode = fields.item(i);
            NodeList children = fieldNode.getChildNodes();
            for(int k = 0; k < children.getLength(); k++){
                Node child = children.item(k);
                String childName = child.getNodeName();
                if(childName.equals("type"))
                    type = MinigameIdentifier.valueOf(child.getTextContent());
                else if(childName.equals("x"))
                    x = Integer.parseInt(child.getTextContent());
                else if(childName.equals("y"))
                    y = Integer.parseInt(child.getTextContent());
                else if(childName.equals("icon")){
                    icon = child.getTextContent();
                    if(!icons.contains(icon))
                        icons.add(icon);
                }
            }
            newBoard.add(new Field(type, x, y, icon));
        }
        
        Log.d("MAP_XML", "Number of fields: " + newBoard.size());
        
        return new Map(imageUrl, newBoard, fieldIconOffsetX, fieldIconOffsetY);
    }
    
    public String getBoardImage() {
        return boardImage;
    }
    
    public List<Field> getBoard(){
        return Collections.unmodifiableList(board);
    }
    
    public int getFieldIconOffsetX() {
        return fieldIconOffsetX;
    }

    public int getFieldIconOffsetY() {
        return fieldIconOffsetY;
    }

    public Field getFieldForPosition(int position) {
        if(board.size() > 0)
            return board.get(position % board.size());
        else
            return null;
    }

    public Field getFieldOfPlayer(Player player) {
        return getFieldForPosition(player.getPosition());  
    }
}
