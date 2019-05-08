import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class Main extends JComponent implements Runnable {
	//イメージ用バッファ
	private BufferedImage mazeAllImg;
	private BufferedImage mazeImg[] = new BufferedImage[6];

	//ぷよ座標
	protected static int myX;
	protected static int myY;

	//サブぷよ位置座標
	protected static int SubX;
	protected static int SubY;

	protected static int FieldWidth = 8;
	protected static int FieldHeight = 14;

	//ぷよスタート位置X座標
	protected static int StartmyX = 3;
	//ぷよスタート位置Y座標
	protected static int StartmyY = 1;

	//設置判定
	static boolean lock = false;

	//ぷよ
	static Random random = new Random();
	static int puyo = random.nextInt(5) + 1;
	static int subpuyo = random.nextInt(5) + 1;

	//回転用
	protected static int a;

	Calendar calendar = Calendar.getInstance();
	//チェック
		public static int checked[][] = {
				//0   1  2  3  4  5  6  7
				{ -1, 0, 0, 0, 0, 0, 0, -1, }, //0
				{ -1, 0, 0, 0, 0, 0, 0, -1, }, //1
				{ -1, 0, 0, 0, 0, 0, 0, -1, }, //2
				{ -1, 0, 0, 0, 0, 0, 0, -1, }, //3
				{ -1, 0, 0, 0, 0, 0, 0, -1, }, //4
				{ -1, 0, 0, 0, 0, 0, 0, -1, }, //5
				{ -1, 0, 0, 0, 0, 0, 0, -1, }, //6
				{ -1, 0, 0, 0, 0, 0, 0, -1, }, //7
				{ -1, 0, 0, 0, 0, 0, 0, -1, }, //8
				{ -1, 0, 0, 0, 0, 0, 0, -1, }, //9
				{ -1, 0, 0, 0, 0, 0, 0, -1, }, //10
				{ -1, 0, 0, 0, 0, 0, 0, -1, }, //11
				{ -1, 0, 0, 0, 0, 0, 0, -1, }, //12
				{ -1, -1, -1, -1, -1, -1, -1, -1, },//13

		};
		public static void checkedriset() {
			for (int i = 0; i < FieldHeight; i++) {
				for (int j = 0; j < FieldWidth; j++) {
					checked[i][j] = 1;
				}
			}
		}



		//フィールド配列

		protected static int fieldData[][] = {
				//0   1  2  3  4  5  6  7
				{ -1, 0, 0, 0, 0, 0, 0, -1, }, //0
				{ -1, 0, 0, 0, 0, 0, 0, -1, }, //1
				{ -1, 0, 0, 0, 0, 0, 0, -1, }, //2
				{ -1, 0, 0, 0, 0, 0, 0, -1, }, //3
				{ -1, 0, 0, 0, 0, 0, 0, -1, }, //4
				{ -1, 0, 0, 0, 0, 0, 0, -1, }, //5
				{ -1, 0, 0, 0, 0, 0, 0, -1, }, //6
				{ -1, 0, 0, 0, 0, 0, 0, -1, }, //7
				{ -1, 0, 0, 0, 0, 0, 0, -1, }, //8
				{ -1, 0, 0, 0, 0, 0, 0, -1, }, //9
				{ -1, 0, 0, 0, 0, 0, 0, -1, }, //10
				{ -1, 0, 0, 0, 0, 0, 0, -1, }, //11
				{ -1, 0, 0, 0, 0, 0, 0, -1, }, //12
				{ -1, -1, -1, -1, -1, -1, -1, -1, },//13

		};

	//スレッド
	Thread th;
	//連結
			public static int getPuyoConectedCount(int x, int y,int z,int count) {
				if(checked[y][x] == 0 || Main.fieldData[y][x] != z) {
					return count;
				}
				count++;
				checked[y][x] = 0;
				if(x + 1 < FieldWidth) {
					count =  getPuyoConectedCount(x+1, y,z,count);
				}

				if(x - 1 > 0) {
					count =  getPuyoConectedCount(x-1, y,z,count);
				}


				if(y  + 1 < FieldHeight)
				{
					count =  getPuyoConectedCount(x, y+1,z,count);

				}
				if(y - 1 > 0)
					count =  getPuyoConectedCount(x, y-1,z,count);


				return count;




			}

			//ぷよ消去
			public static void erase(int x, int y,int z) {
				if(Main.fieldData[y][x] != z) {
					return;
				}
				Main.fieldData[y][x] = 0;
			if(x + 1 < FieldWidth) {
				erase(x+1, y,z);
			}

			if(x - 1 > 0) {
				erase(x-1, y,z);
			}


			if(y  + 1 < FieldHeight)
			{
				erase(x, y+1,z);

			}
			if(y - 1 > 0)
			{
				erase(x, y-1,z);
			}
			}



			public static void Puyoerase() {
				for(int i = 0; i < FieldHeight - 1; i++) {
					for(int j = 1; j < FieldWidth - 1; j++) {

						checkedriset();

						if(Main.fieldData[i][j] != 0) {

						if(getPuyoConectedCount(j,i,Main.fieldData[i][j],0) >= 4) {

							erase(j,i,fieldData[i][j]);

						}

						}
						rakka();
					}
				}
			}






	//浮いてたら落下

	public static void rakka() {
		for(int y = FieldHeight - 3; y >= 0; y--) {
			for(int x = 1; x < FieldWidth-1; x++) {
				if(Main.fieldData[y][x] != 0 &&
						Main.fieldData[y+ 1][x] == 0) {
					Main.fieldData[y+ 1][x] = Main.fieldData[y][x];
					Main.fieldData[y][x] = 0;


				}


			}
		}
	}
	//固定
	public static void kotei() {
		if ((Main.fieldData[myY + 1][myX] == -1 || Main.fieldData[myY + 1][myX] != 0)
				|| ((Main.fieldData[SubY + 1][SubX] == -1 || Main.fieldData[SubY + 1][SubX] != 0))) {
			Main.fieldData[myY][myX] = puyo;
			Main.fieldData[SubY][SubX] = subpuyo;
			Main.lock = true;


			//浮いてたら落下
			if (Main.lock) {

				Main.lock = false;
				rakka();

			}
			Puyoerase();


			//スタート位置に戻る
			Main.myY = Main.StartmyY;
			Main.myX = Main.StartmyX;
			Main.SubX = Main.myX;
			Main.SubY = Main.myY - 1;

			int r = new java.util.Random().nextInt(5) + 1;
			int r1 = new java.util.Random().nextInt(5) + 1;
			Main.puyo = r;
			Main.subpuyo = r1;

		}

	}



	public static void main(String[] args) {
		// TODO 自動生成されたメソッド・スタブ

		Main main = new Main();
		main.setPreferredSize(new Dimension(258, 450));

		JFrame frame = new JFrame("ぷよぷよ");
		frame.setBounds(400, 50, 0, 0);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setBackground(Color.BLACK);
		frame.setVisible(true);
		frame.add(main);
		frame.pack();

		KeyContainer kc = new KeyContainer();
		frame.addKeyListener(kc);

	}
//メインメソッド
	Main() {
		myX = StartmyX;
		myY = StartmyY;
		SubX = myX;
		SubY = myY - 1;
		a = 1;
		checkedriset();

		loadPuyoImage();
		TimeLabel label = new TimeLabel();
		th = new Thread(this);
		th.start();

	}

	public void loadPuyoImage() {
		// イメージ取得
		try {
			File openFile = new File("img/Charpuyo.png");
			this.mazeAllImg = ImageIO.read(openFile);
		} catch (IOException ex) {
			System.out.println("Image Load Error");
		}

		// イメージ切り分け
		//赤ぷよ １
		mazeImg[1] = new BufferedImage(32, 32, this.mazeAllImg.getType());
		mazeImg[1] = this.mazeAllImg.getSubimage(1 * 32, 0, 32, 32);
		//黄ぷよ 2
		mazeImg[2] = new BufferedImage(32, 32, this.mazeAllImg.getType());
		mazeImg[2] = this.mazeAllImg.getSubimage(2 * 32, 0, 32, 32);
		//緑ぷよ 3
		mazeImg[3] = new BufferedImage(32, 32, this.mazeAllImg.getType());
		mazeImg[3] = this.mazeAllImg.getSubimage(3 * 32, 0, 32, 32);
		//青ぷよ 4
		mazeImg[4] = new BufferedImage(32, 32, this.mazeAllImg.getType());
		mazeImg[4] = this.mazeAllImg.getSubimage(4 * 32, 0, 32, 32);
		//紫ぷよ 5
		mazeImg[5] = new BufferedImage(32, 32, this.mazeAllImg.getType());
		mazeImg[5] = this.mazeAllImg.getSubimage(5 * 32, 0, 32, 32);
		//壁  -1
		mazeImg[0] = new BufferedImage(32, 32, this.mazeAllImg.getType());
		mazeImg[0] = this.mazeAllImg.getSubimage(0 * 32, 0, 32, 32);

	}


	/**********************************************************************/
	@Override
	public void run() {
		// TODO 自動生成されたメソッド・スタブ
		while (true) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {

					//地面についたら固定
					kotei();
					//Puyoerase();



				}
			});

			try {
				Thread.sleep(36);
			} catch (InterruptedException e) {
			}

			repaint();
		}
	}

	//描画処理
	@Override
	public void paintComponent(Graphics g) {

		for (int y = 0; y < 14; y++) {
			for (int x = 0; x < 8; x++) {

				if (fieldData[y][x] == -1) {
					g.drawImage(mazeImg[0], x * 32, y * 32, this);
				}
				if (fieldData[y][x] == 1) {
					g.drawImage(mazeImg[1], x * 32, y * 32, this);
				}
				if (fieldData[y][x] == 2) {
					g.drawImage(mazeImg[2], x * 32, y * 32, this);
				}
				if (fieldData[y][x] == 3) {
					g.drawImage(mazeImg[3], x * 32, y * 32, this);
				}
				if (fieldData[y][x] == 4) {
					g.drawImage(mazeImg[4], x * 32, y * 32, this);
				}
				if (fieldData[y][x] == 5) {
					g.drawImage(mazeImg[5], x * 32, y * 32, this);
				}

			}
		}
		g.drawImage(mazeImg[puyo], myX * 32, myY * 32, this);
		g.drawImage(mazeImg[subpuyo], SubX * 32, SubY * 32, this);

	}
}

class KeyContainer implements KeyListener {
	/**************************************************************************/
	/* キーが押された瞬間													  */
	/**************************************************************************/
	@Override
	public void keyTyped(KeyEvent e) {
		// TODO 自動生成されたメソッド・スタブ

	}

	/**************************************************************************/
	/* キーを押している時													  */
	/**************************************************************************/
	@Override
	public void keyPressed(KeyEvent e) {
		// TODO 自動生成されたメソッド・スタブ

		switch (e.getKeyCode()) {
		case KeyEvent.VK_DOWN: //下移動
			if (Main.fieldData[Main.myY + 1][Main.myX] != -1) {

				Main.myY++;
				Main.SubY++;
			}

			break;

		case KeyEvent.VK_LEFT: //左
			if ((Main.fieldData[Main.myY][Main.myX - 1] != -1 && Main.fieldData[Main.SubY][Main.SubX - 1] != -1) &&
					!(Main.fieldData[Main.myY][Main.myX - 1] >= 1 || Main.fieldData[Main.SubY][Main.SubX - 1] >= 1)) {

				Main.myX--;
				Main.SubX--;

			}

			break;

		case KeyEvent.VK_RIGHT: //右移動
			if ((Main.fieldData[Main.myY][Main.myX + 1] != -1 && Main.fieldData[Main.SubY][Main.SubX + 1] != -1) &&
					!(Main.fieldData[Main.myY][Main.myX + 1] >= 1 || Main.fieldData[Main.SubY][Main.SubX + 1] >= 1)) {

				Main.myX++;
				Main.SubX++;

			}

			break;
		case KeyEvent.VK_SPACE: //回転

			if (Main.fieldData[Main.SubY][Main.SubX  + 1] == 0 || Main.fieldData[Main.SubY][Main.SubX - 1] == 0 ) {

		

				Main.a = Main.a % 4;
				switch (Main.a) {
				case 1:
					if (Main.fieldData[Main.SubY][Main.SubX - 1] != -1 ||
							Main.fieldData[Main.SubY][Main.SubX - 1] >= 1) {
						Main.SubX = Main.myX - 1;
						Main.SubY = Main.myY;
						Main.a++;
					}

					break;
				case 2:
					if (Main.fieldData[Main.SubY + 1][Main.SubX] != -1 ||
							Main.fieldData[Main.SubY + 1][Main.SubX] >= 1) {
						Main.SubX = Main.myX;
						Main.SubY = Main.myY + 1;
						Main.a++;
					}
					break;
				case 3:
					if (Main.fieldData[Main.SubY][Main.SubX + 1] != -1 ||
							Main.fieldData[Main.SubY][Main.SubX + 1] >= 1) {
						Main.SubX = Main.myX + 1;
						Main.SubY = Main.myY;
						Main.a++;
					}
					break;
				case 0:

					Main.SubX = Main.myX;
					Main.SubY = Main.myY - 1;
					Main.a++;
			
			break;
				}
		}
		}
	}

	/**************************************************************************/
	/* キーが放された瞬間													  */
	/**************************************************************************/
	@Override
	public void keyReleased(KeyEvent e) {
		// TODO 自動生成されたメソッド・スタブ

	}
}

class TimeLabel {

	public TimeLabel() {

		Timer t = new Timer();
		t.schedule(new TimerLabelTask(), 0, 1000);
	}

	public void setTime() {
		if (Main.fieldData[Main.myY + 1][Main.myX] == 0 || Main.fieldData[Main.SubY + 1][Main.SubX] == 0) {
			Main.myY++;
			Main.SubY++;
		}

	}

	class TimerLabelTask extends TimerTask {

		public void run() {
			setTime();
		}
	}
}
