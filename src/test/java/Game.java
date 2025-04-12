import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JFrame;
import javax.swing.JPanel;

// Lớp trừu tượng State định nghĩa các phương thức cơ bản
abstract class State {
    protected GameStateManager gsm;

    public State(GameStateManager gsm) {
        this.gsm = gsm;
    }

    public abstract void update();
    public abstract void render(Graphics g);
    public abstract void keyPressed(int key);
}

// Trạng thái Menu
class MenuState extends State {
    public MenuState(GameStateManager gsm) {
        super(gsm);
    }

    @Override
    public void update() {
        // Logic cập nhật cho menu (nếu có)
    }

    @Override
    public void render(Graphics g) {
        g.drawString("Main Menu - Press Enter to Start", 50, 100);
    }

    @Override
    public void keyPressed(int key) {
        if (key == KeyEvent.VK_ENTER) {
            gsm.setState(new GameState(gsm)); // Chuyển sang trạng thái Game
        }
    }
}

// Trạng thái Gameplay
class GameState extends State {
    private int playerX = 50; // Vị trí người chơi

    public GameState(GameStateManager gsm) {
        super(gsm);
    }

    @Override
    public void update() {
        // Cập nhật logic game, ví dụ di chuyển nhân vật
        playerX += 1; // Nhân vật di chuyển sang phải
        if (playerX > 300) playerX = 50; // Reset khi ra khỏi màn hình
    }

    @Override
    public void render(Graphics g) {
        g.drawString("Gameplay - Press Esc to return to Menu", 50, 50);
        g.fillRect(playerX, 150, 20, 20); // Vẽ nhân vật đơn giản
    }

    @Override
    public void keyPressed(int key) {
        if (key == KeyEvent.VK_ESCAPE) {
            gsm.setState(new MenuState(gsm)); // Quay lại Menu
        }
    }
}

// Quản lý các trạng thái
class GameStateManager {
    private State currentState;

    public GameStateManager() {
        currentState = new MenuState(this); // Bắt đầu từ Menu
    }

    public void setState(State state) {
        this.currentState = state;
    }

    public void update() {
        currentState.update();
    }

    public void render(Graphics g) {
        currentState.render(g);
    }

    public void keyPressed(int key) {
        currentState.keyPressed(key);
    }
}

// Panel chính để vẽ và xử lý game
class GamePanel extends JPanel implements KeyListener, Runnable {
    private GameStateManager gsm;
    private boolean running = true;

    public GamePanel() {
        gsm = new GameStateManager();
        setFocusable(true);
        addKeyListener(this);
        new Thread(this).start(); // Bắt đầu luồng game
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        gsm.render(g);
    }

    @Override
    public void run() {
        while (running) {
            gsm.update();
            repaint();
            try {
                Thread.sleep(16); // ~60 FPS
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        gsm.keyPressed(e.getKeyCode());
    }

    @Override
    public void keyTyped(KeyEvent e) {}
    @Override
    public void keyReleased(KeyEvent e) {}
}

// Lớp chính để chạy game
public class Game extends JFrame {
    public Game() {
        setTitle("Java Game with State Pattern");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        add(new GamePanel());
        setVisible(true);
    }

    public static void main(String[] args) {
        new Game();
    }
}