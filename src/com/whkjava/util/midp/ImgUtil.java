package com.whkjava.util.midp;

/**
 * <p>Title: KMPoPo</p>
 * <p>Description: KMPoPo</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author HUAKUI_WANG
 * @version 1.0
 */

import java.io.IOException;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

public class ImgUtil
{
    public ImgUtil()
    {
    }

    public static Image createImage(String s)
    {
        Image image = null;
        try
        {
            image = Image.createImage(s);
        }
        catch(IOException ioexception)
        {
            ioexception.printStackTrace();
        }
        return image;
    }

    public static void drawImage(Graphics g, Image image, int i, int j)
    {
        g.drawImage(image, i, j, 20);
    }

    public static void drawString(Graphics g, String s, int x, int y)
    {
        g.drawString(s, x, y, 20);
    }

    public static void drawOutlineString(Graphics g, String s, int i, int j, int k, int l, int i1, int j1,
            int k1, int l1)
    {
        g.setColor(k, l, i1);
        drawString(g, s, i - 1, j - 1);
        drawString(g, s, i, j - 1);
        drawString(g, s, i + 1, j - 1);
        drawString(g, s, i - 1, j);
        drawString(g, s, i + 1, j);
        drawString(g, s, i - 1, j + 1);
        drawString(g, s, i, j + 1);
        drawString(g, s, i + 1, j + 1);
        g.setColor(j1, k1, l1);
        drawString(g, s, i, j);
    }
}
