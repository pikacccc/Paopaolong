package com.whkjava.popo;

/**
 * <p>Title: KMPoPo</p>
 * <p>Description: KMPoPo</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 *
 * @author HUAKUI_WANG
 * @version 1.0
 */

import javax.microedition.lcdui.Canvas;
import java.io.*;
import javax.microedition.lcdui.*;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;

import com.whkjava.util.midp.ImgUtil;
//import com.motorola.game.*;

class GameCanvas
        extends Canvas
        implements Runnable, CommandListener {

    public static final int OFSET = -4;
    public static final int ONSIGN = -1;
    public static final int ONTITLE = 0;
    public static final int ONGAME = 1;
    public static final int ONOPTION = 2;
    public static final int STGOFX = 16;
    public static final int STGOFY = 20;
    public static final int S_PYONPA = 0;
    public static final int S_ERASE = 1;
    public static final int S_SHOT = 2;
    public static final int S_GOVER = 3;
    public static int trX;
    public static int trY;

    private int iS;
    private int pS;
    private int bgS;
    private int mV;
    private int sC;
    private int dR;
    private int lV;
    private int dV;
    private int eC;
    private int bR;
    private int kY;
    private int bobP;
    private int bAnim;
    private int mH;
    private int mS;
    private int eB;
    private int dB;
    private boolean sE;
    private boolean kF;
    private boolean gF;
    private boolean dF;
    private boolean upA;
    private boolean upB;
    private Bobble pA;
    //private Font sFont;

    public Image ofSc1;
    public Image ofSc2;
    public Graphics ofSg2;
    public Image ofSc3;
    public Image bomI;
    public Image awaI[];
    public Image plyI[];
    public Image numI[];
    private int aWidth;

    private KMPoPoMIDlet m_MIDlet;
    private Thread mThread;
    private RecordStore rStore;

    private Command cmdPause;
    private Command cmdExit;
    private Command cmdStart;
    private Command cmdContinue;

    private boolean pauseFlag;
    private boolean resumeFlag;
    private boolean mNewGameFlg;
    //private SoundEffect soundShot;

    public GameCanvas(KMPoPoMIDlet midlet) {
        resumeFlag = false;
        pauseFlag = false;
        rStore = null;

        try {
            rStore = RecordStore.openRecordStore("KMPOPO", true);
        } catch (Exception exception) {
        }
        m_MIDlet = midlet;

        loadData();
        loadImages();
        setBGImage();

        bgS = 0;

        cmdStart = new Command("Start", 1, 1);
        cmdPause = new Command("Pause", 1, 1);
        cmdContinue = new Command("Continue", 1, 1);
        cmdExit = new Command("Exit", 1, 2);
        addCommand(cmdPause);
        addCommand(cmdExit);
        setCommandListener(this);

        mNewGameFlg = true;
        mThread = new Thread(this);
        mThread.start();

        init(mH, sE);
    }

    private void loadData() {
        if (rStore == null) {
            return;
        }
        Object obj = null;
        try {
            if (rStore.getNumRecords() == 0) {
                ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
                DataOutputStream dataoutputstream = new DataOutputStream(
                        bytearrayoutputstream);
                try {
                    dataoutputstream.writeInt(0);
                    dataoutputstream.writeBoolean(false);
                } catch (IOException ioexception) {
                }
                byte abyte0[] = bytearrayoutputstream.toByteArray();
                rStore.addRecord(abyte0, 0, abyte0.length);
            } else {
                byte abyte1[] = rStore.getRecord(1);
                if (abyte1 != null) {
                    ByteArrayInputStream bytearrayinputstream = new ByteArrayInputStream(
                            abyte1);
                    DataInputStream datainputstream = new DataInputStream(
                            bytearrayinputstream);
                    mH = datainputstream.readInt();
                    sE = datainputstream.readBoolean();
                } else {
                    mH = 0;
                    sE = false;
                }
            }
        } catch (Exception exception) {
        }
    }

    private Image LoadFromImage(Image oldImage, int x, int y, int nStep, int ntotal) {

        Image image = Image.createImage(x, y);
        Graphics g = image.getGraphics();
        ImgUtil.drawImage(g, oldImage, -(nStep % ntotal) * x, 0);
        return image;
    }

    private boolean loadImages() {
        bomI = ImgUtil.createImage("/imgs/bom.png");
        awaI = new Image[8];
        for (int i = 0; i < awaI.length; i++) {
            awaI[i] = ImgUtil.createImage("/imgs/awa" + (i + 1) + ".png");
        }

        numI = new Image[10];
        Image imgtmp = ImgUtil.createImage("/imgs/num.png");
        for (int j = 0; j < numI.length; j++) {
            numI[j] = LoadFromImage(imgtmp, 8, 16, j, 10);
        }

        plyI = new Image[5];

        for (int k = 0; k < plyI.length; k++) {
            plyI[k] = ImgUtil.createImage("/imgs/bub" + (k + 1) + ".png");
        }

        return true;
    }

    public boolean setBGImage() {
        ofSc1 = ImgUtil.createImage("/imgs/bg.png");
        ofSc3 = ImgUtil.createImage("/imgs/sg.png");
        ofSc2 = Image.createImage(ofSc3.getWidth(), ofSc3.getHeight());
        ofSg2 = ofSc2.getGraphics();
        return true;
    }

    public void init(int i, boolean flag) {
        iS = 0;
        pS = 0;
        lV = 0;
        dV = 0;
        eC = 0;
        sC = 0;
        bR = 0;
        dR = 0xbbb61;
        mV = 21;
        kF = false;
        dF = false;
        mS = 0;
        eB = 0;
        dB = 0;
        bobP = 0;
        bAnim = 0;
        upA = false;
        upB = false;
        mH = i;
        sE = flag;
        aWidth = awaI[0].getWidth();
        if (pA == null) {
            pA = new Bobble();
        }
        Bobble.init();
        Bobble.sAID();
        drawBobbles(ofSg2, 0);
        gF = true;
        if (!mThread.isAlive()) {
            mThread = new Thread(this);
            mThread.start();
        }
        mNewGameFlg = false;
    }

    private void drawGameOver(Graphics g) {
        int i = Font.getDefaultFont().getHeight();
        g.setColor(0xffff00);
        g.drawString("GAME",
                (ofSc3.getWidth() - Font.getDefaultFont().stringWidth("GAME")) /
                        2 + 4, 32, 20);
        g.drawString("OVER",
                (ofSc3.getWidth() - Font.getDefaultFont().stringWidth("OVER")) /
                        2 + 4, 34 + i, 20);
    }

    private void drawBobbles(Graphics g, int i) {
        int i2 = 0;
        int j = sC;
        int l = bR & 0xff;
        if (j >= ((dR >> dV * 2 & 3) + 3) - 2) {
            if (j == ((dR >> dV * 2 & 3) + 3) - 2) {
                if ((l & 3) == 3) {
                    i2 = 1;
                }
            } else if ((l & 1) == 1) {
                i2 = 1;
            }
            if (l >= 240) {
                bR = 0;
            } else {
                bR++;
            }
        }
        g.drawImage(ofSc3, 0, 0, 20);
        upB = false;
        for (int k1 = 9; k1 >= 0; k1--) {
            int i1 = 0;
            if (Bobble.aA[5 + 6 * k1] == -1) {
                i1 = aWidth / 2;
            }
            for (int j1 = 0; j1 < 6; j1++) {
                int l1 = j1 + 6 * k1;
                int k = Bobble.aA[l1];
                if (k > 0) {
                    if ((k & 0x10) == 16) {
                        if (pS == 4) {
                            k &= 0xf;
                            g.drawImage(bomI, j1 * aWidth + i1, k1 * aWidth, 20);
                        } else {
                            k &= 0xf;
                            g.drawImage(awaI[k - 1], j1 * aWidth + i1,
                                    k1 * aWidth + aWidth / 2, 20);
                        }
                        Bobble.cBA(l1);
                    } else {
                        k &= 0xf;
                        if (pS == -1) {
                            if (k1 >= i) {
                                g.drawImage(awaI[7], j1 * aWidth + i1, k1 * aWidth, 20);
                            } else {
                                g.drawImage(awaI[k - 1], j1 * aWidth + i1, k1 * aWidth, 20);
                            }
                        } else {
                            g.drawImage(awaI[k - 1], j1 * aWidth + i1, k1 * aWidth + i2, 20);
                            if (k1 > 5) {
                                upB = true;
                            }
                        }
                    }
                }
            }

        }

        drawMoveBobble(g);
    }

    public void drawMoveBobble(Graphics g) {
        int k = Bobble.aN - 1;
        g.drawImage(awaI[k], ofSc2.getWidth() - aWidth, ofSc2.getHeight() - aWidth,
                20);
        if (pS >= 0) {
            g.drawImage(plyI[bobP], 0, ofSc2.getHeight() - plyI[bobP].getHeight(), 20);
            if (bobP > 0) {
                bobP = 0;
            }
        } else {
            g.drawImage(plyI[4], 0, ofSc2.getHeight() - plyI[bobP].getHeight(), 20);
        }
        if (upA) {
            mV = 21;
            upA = false;
        }
        int i = 28 + (Bobble.CO[mV] << 4)*2 / 1000 + -4;
        int j = 84 + (Bobble.SI[mV] << 4)*2 / 1000 + -4;
        k = 28 + (Bobble.CO[(21 - mV) + 21] * 42) / 1000 + -4;
        int i1 = 84 + (Bobble.SI[(21 - mV) + 21] * -42) / 1000 + -4;
        g.setColor(255, 255, 0);
        g.drawLine(i, j, k, i1);
        g.drawLine(i, j, ARW1[mV][0] + -4, ARW1[mV][1] + -4);
        g.drawLine(i, j, ARW2[mV][0] + -4, ARW2[mV][1] + -4);
        if (gF && pS < 3) {
            int l = Bobble.aM - 1;
            g.drawImage(awaI[l], Bobble.aX - aWidth / 2, Bobble.aY - aWidth / 2,
                    20);
        } else if (pS == 0) {
            g.drawImage(awaI[Bobble.aM - 1], Bobble.aX - aWidth / 2,
                    Bobble.aY - aWidth / 2, 20);
        }
    }

    private void drawScore(Graphics g) {
        int i = lV + 1;
        int j = mS;
        if (j > 99999) {
            j = 99999;
        }
        if (mS > mH) {
            mH = mS;
            setGConf(mH, sE);
            saveData();
        }
        int k = mH;
        if (k > 99999) {
            k = 99999;
        }
        try {
            //Level
            char ac[] = Integer.toString(i).toCharArray();
            int l = ac.length;
            for (int i1 = 0; i1 < l; i1++) {
                g.drawImage(numI[Character.digit(ac[i1], 10)], 80 + (5 - (l - i1)) * 8,
                        24, 20);
            }
            //score
            ac = Integer.toString(j).toCharArray();
            l = ac.length;
            for (int j1 = 0; j1 < l; j1++) {
                g.drawImage(numI[Character.digit(ac[j1], 10)], 80 + (5 - (l - j1)) * 8,
                        58, 20);
            }
            //hi-score
            ac = Integer.toString(k).toCharArray();
            l = ac.length;
            for (int k1 = 0; k1 < l; k1++) {
                g.drawImage(numI[Character.digit(ac[k1], 10)], 80 + (5 - (l - k1)) * 8,
                        92, 20);

            }
        } catch (Exception exception) {
        }
    }

    public void setGConf(int i, boolean flag) {
        mH = i;
        sE = flag;
    }

    public void saveData() {
        if (rStore == null) {
            return;
        }
        ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
        DataOutputStream dataoutputstream = new DataOutputStream(
                bytearrayoutputstream);
        try {
            dataoutputstream.writeInt(mH);
            dataoutputstream.writeBoolean(sE);
        } catch (IOException ioexception) {
        }
        byte abyte0[] = bytearrayoutputstream.toByteArray();
        try {
            rStore.setRecord(1, abyte0, 0, abyte0.length);
        } catch (RecordStoreException recordstoreexception) {
        }
    }

    public void commandAction(Command command, Displayable displayable) {
        if (command == cmdExit) {
            m_MIDlet.notifyDestroyed();
        } else if (command == cmdPause) {
            pauseFlag = true;
            removeCommand(cmdPause);
            addCommand(cmdContinue);
        } else if (command == cmdContinue) {
            pauseFlag = false;
            removeCommand(cmdContinue);
            addCommand(cmdPause);
        }
        if (command == cmdStart) {
            retryPlease();
            removeCommand(cmdStart);
            addCommand(cmdPause);
        }
    }

    protected void hideNotify() {
        pauseFlag = true;
    }

    protected void showNotify() {
        pauseFlag = false;
        resumePlease();
    }

    public void resumePlease() {
        resumeFlag = true;
        if (pS < 0) {
            drawBobbles(ofSg2, 0);
            drawGameOver(ofSg2);
        }
        repaint();
        serviceRepaints();
    }

    public void retryPlease() {
        init(mH, sE);
        repaint();
    }

    public void run() {
        byte byte0 = 0;

        while (mNewGameFlg) {
            try {
                Thread.sleep(100L);
            } catch (Exception exception1) {
            }
        }

        while (gF) {
            while (pauseFlag) {
                try {
                    Thread.sleep(100L);
                } catch (Exception exception1) {
                }
            }
            try {
                if (pS == 4) {
                    Thread.sleep(100L);
                } else if (pS == 5) {
                    Thread.sleep(200L);
                } else {
                    Thread.sleep(10L);
                }
            } catch (Exception exception2) {
            }
            if (pS == 2) {
                for (int i = 0; i < 8; i++) {
                    int k;
                    if ((k = Bobble.move()) >= 2) {
                        continue;
                    }
                    if (k == 0) {
                        pS = 3;
                    }
                    break;
                }

            } else if (pS == 3) {
                int l = Bobble.bom();
                if (l > 0) {
                    eB += l;
                    eC += l;
                    mS += l * 10;
                    pS = 4;
                } else {
                    pS = 5;
                }
            } else if (pS == 4) {
                int i1 = Bobble.drp();
                if (i1 > 0) {
                    dB += i1;
                    eC += i1;
                    if (i1 > 12) {
                        i1 = 12;
                    }
                    mS += 20 << i1 - 1;
                }
                pS = 5;
            } else if (pS == 5) {
                if (sC >= (dR >> dV * 2 & 3) + 3) {
                    sC = 0;
                    Bobble.dtR(dV);
                }
                if (Bobble.nA == 0) {
                    mS += 5000 * (lV + 1);
                }
                if (eC >= 47 && sC == 0) {
                    lV++;
                    dV++;
                    if (dV > 9) {
                        dV = lV - (lV / 10) * 10;
                        if (lV % 10 != 0) {
                            ;
                        }
                    }
                    eC -= 47;
                }
                pS = 6;
            } else if (pS == 6) {
                for (int j = 59; j >= 54; j--) {
                    if (Bobble.aA[j] > 0) {
                        gF = false;
                    }
                }

                Bobble.sAID();
                pS = 1;
            }
            if (kF) {
                if (kY == -3 && mV > 0) {
                    mV--;
                } else if (kY == -4 && mV < 42) {
                    mV++;
                }
            }
            if (pS > 0) {
                drawBobbles(ofSg2, 0);
                repaint();
                serviceRepaints();
            }
            if (bAnim >= 10) {
                resumeFlag = true;
                if (upB) {
                    bobP = 3;
                    if (bAnim == 20) {
                        bAnim = 0;
                    } else {
                        bAnim++;
                    }
                } else if (byte0 == 1) {
                    byte0 = 2;
                    bobP = 2;
                    bAnim = 0;
                } else if (byte0 == 2) {
                    if (bAnim == 20) {
                        byte0 = 0;
                        bAnim = 0;
                    } else {
                        bobP = 2;
                        bAnim++;
                    }
                } else {
                    byte0 = 1;
                    bobP = 1;
                    bAnim = 0;
                }
            } else {
                bAnim++;
                if (byte0 == 2) {
                    bobP = 2;
                }
            }
        }

        pS = -1;
        if (!dF) {
            for (int j1 = 8; j1 >= 0; j1--) {
                try {
                    Thread.sleep(10L);
                } catch (Exception exception3) {
                }
                drawBobbles(ofSg2, j1);
                if (j1 == 0) {
                    drawGameOver(ofSg2);
                }
                repaint();
                serviceRepaints();
            }
        }
        pS = -2;
        removeCommand(cmdPause);
        removeCommand(cmdContinue);
        addCommand(cmdStart);
    }

    protected void keyPressed(int i) {
        //Left Key code -3
        //Right keycode -4
        //Center keycode -5

        int key = getGameAction(i);
        if (pauseFlag) return;

        if (i == -3 || i == -4) {
            if (gF && !kF) {
                kY = i;
                kF = true;
            }
        } else if (i == -5) {
            if (gF && pS == 1) {
                System.gc();
                kF = false;
                synchronized (this) {
                    pS = 2;
                }
                Bobble.mD = mV;
                sC++;
            }
        }
    }

    protected void keyReleased(int i) {
        int k = i;
        if (k == -3 || k == -4) {
            kF = false;
        }
    }

    public void paint(Graphics g) {
        if (pS == 0 || resumeFlag) {
            g.setColor(0x00ff00);
            g.fillRect(0, 0, getWidth(), getHeight());
            g.drawImage(ofSc1, 0, 0, 20);
            if (pS == 0) {
                pS = 1;
            }
            resumeFlag = false;
        }
        g.drawImage(ofSc2, 16, 20, 20);
        drawScore(g);
    }

    private static final int ARW1[][] = {
            {
                    16, 82
            }
            , {
            16, 81
    }
            , {
            16, 80
    }
            , {
            16, 79
    }
            , {
            17, 79
    }
            , {
            17, 77
    }
            , {
            18, 77
    }
            , {
            18, 76
    }
            , {
            19, 75
    }
            , {
            20, 74
    }
            , {
            20, 74
    }
            , {
            21, 73
    }
            , {
            22, 73
    }
            , {
            23, 72
    }
            , {
            24, 72
    }
            , {
            25, 72
    }
            , {
            25, 71
    }
            , {
            26, 71
    }
            , {
            27, 72
    }
            , {
            28, 72
    }
            , {
            29, 72
    }
            , {
            29, 71
    }
            , {
            30, 72
    }
            , {
            31, 72
    }
            , {
            32, 72
    }
            , {
            33, 72
    }
            , {
            33, 72
    }
            , {
            34, 73
    }
            , {
            35, 73
    }
            , {
            36, 74
    }
            , {
            37, 75
    }
            , {
            38, 75
    }
            , {
            38, 76
    }
            , {
            38, 77
    }
            , {
            39, 78
    }
            , {
            40, 79
    }
            , {
            39, 80
    }
            , {
            40, 80
    }
            , {
            40, 82
    }
            , {
            41, 82
    }
            , {
            40, 83
    }
            , {
            40, 84
    }
            , {
            40, 85
    }
    };
    private static final int ARW2[][] = {
            {
                    16, 85
            }
            , {
            16, 84
    }
            , {
            16, 83
    }
            , {
            15, 82
    }
            , {
            16, 82
    }
            , {
            16, 80
    }
            , {
            17, 80
    }
            , {
            16, 79
    }
            , {
            17, 78
    }
            , {
            18, 77
    }
            , {
            18, 76
    }
            , {
            18, 75
    }
            , {
            19, 75
    }
            , {
            20, 74
    }
            , {
            21, 73
    }
            , {
            22, 73
    }
            , {
            23, 72
    }
            , {
            23, 72
    }
            , {
            24, 72
    }
            , {
            25, 72
    }
            , {
            26, 72
    }
            , {
            27, 71
    }
            , {
            27, 72
    }
            , {
            28, 72
    }
            , {
            29, 72
    }
            , {
            30, 71
    }
            , {
            31, 71
    }
            , {
            31, 72
    }
            , {
            32, 72
    }
            , {
            33, 72
    }
            , {
            34, 73
    }
            , {
            35, 73
    }
            , {
            36, 74
    }
            , {
            36, 74
    }
            , {
            37, 75
    }
            , {
            38, 76
    }
            , {
            38, 77
    }
            , {
            39, 77
    }
            , {
            39, 79
    }
            , {
            40, 79
    }
            , {
            40, 80
    }
            , {
            40, 81
    }
            , {
            40, 82
    }
    };

}
