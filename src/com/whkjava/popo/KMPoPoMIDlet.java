package com.whkjava.popo;

/**
 * <p>Title: KMPoPo</p>
 * <p>Description: KMPoPo</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author HUAKUI_WANG
 * @version 1.0
 */

import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDlet;
import com.whkjava.util.midp.ImgUtil;

public class KMPoPoMIDlet extends MIDlet {
  private boolean soundflag = true;

  public KMPoPoMIDlet()
  {
      Display.getDisplay(this).setCurrent(new GameCanvas(this));
  }

  protected void startApp()
  {
  }

  protected void pauseApp()
  {
  }

  protected void destroyApp(boolean flag)
  {
  }

  public boolean GetSoundState()
  {
      return soundflag;
  }

  public void ResetSoundState()
  {
      soundflag = true;
  }

  public void SetSoundState()
  {
      soundflag = false;
  }
}
