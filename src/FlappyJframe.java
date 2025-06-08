import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;

public class FlappyJframe extends JPanel implements ActionListener, KeyListener {
    int boardWidth = 360;
    int boardHeight = 640;

    // Görseller
    Image backgroundImg;
    Image birdImg;
    Image topPipeImg;
    Image bottomPipeImg;

    int birdX = boardWidth / 8;
    int birdY = boardHeight / 2;
    int birdWidth = 32;
    int birdHeight = 24;

    // Borular
    int pipeX = boardWidth;
    int pipeY = 0;
    int pipeWidth = 64;
    int pipeHeight = 512;

    // Puan
    int score = 0;

    class Pipe {
        int x = pipeX;
        int y = pipeY;
        int height = pipeHeight;
        int width = pipeWidth;
        Image img;
        boolean passed = false; // Borunun geçilip geçilmediğini kontrol etmek için

        public Pipe(Image img) {
            this.img = img;
        }
    }

    class Bird {
        int x = birdX;
        int y = birdY;
        int width = birdWidth;
        int height = birdHeight;
        Image img;

        Bird(Image img) {
            this.img = img;
        }
    }

    // Oyun mantığı
    Bird bird1;
    int velocityY = 0;
    int gravity = 1;
    int velocityX = -4;

    ArrayList<Pipe> pipes;

    Timer gameLoop;
    Timer placePipesTimer;
    boolean gameOver = false;

    FlappyJframe() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
         
        setFocusable(true);
        addKeyListener(this);
        setBackground(Color.blue);

        // Görselleri yükle
        try {
            backgroundImg = new ImageIcon(getClass().getResource("./flappybirdbg.png")).getImage();
            birdImg = new ImageIcon(getClass().getResource("./flappybird.png")).getImage();
            topPipeImg = new ImageIcon(getClass().getResource("./toppipe.png")).getImage();
            bottomPipeImg = new ImageIcon(getClass().getResource("./bottompipe.png")).getImage();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Görseller yüklenemedi!", "Hata", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        // Kuş ve boruları başlat
        bird1 = new Bird(birdImg);
        pipes = new ArrayList<>();

        // Boruları yerleştirme zamanlayıcısı
        placePipesTimer = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                placePipes();
            }
        });
        placePipesTimer.start();

        // Oyun döngüsü
        gameLoop = new Timer(1000 / 60, this);
        gameLoop.start();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void move() {
        velocityY += gravity;
        birdY += velocityY;
        birdY = Math.max(birdY, 0); // Kuşun ekranın üstünden çıkmasını engelle
        birdY = Math.min(birdY, boardHeight - birdHeight); // Kuşun ekranın altından çıkmasını engelle

        for (int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            pipe.x += velocityX;

            // Ekran dışına çıkan boruları kaldır
            if (pipe.x + pipe.width < 0) {
                pipes.remove(i);
                i--;
            }

            // Boruyu geçtiğinde puanı artır
            if (!pipe.passed && birdX > pipe.x + pipe.width) {
                pipe.passed = true;
                score++;
            }
        }

        // Yere çarptığında veya borularla çarpıştığında oyunu bitir
        if (birdY >= boardHeight - birdHeight || checkCollision()) {
            gameOver = true;
        }
    }

    public boolean checkCollision() {
        Rectangle birdRect = new Rectangle(birdX, birdY, birdWidth, birdHeight);
        for (Pipe pipe : pipes) {
            Rectangle pipeRect = new Rectangle(pipe.x, pipe.y, pipe.width, pipe.height);
            if (birdRect.intersects(pipeRect)) {
                return true;
            }
        }
        return false;
    }

    public void placePipes() {
        int randomPipeY = (int) (pipeY - pipeHeight / 4 - Math.random() * (pipeHeight / 2));
        int openingSpace = boardHeight / 4;

        Pipe topPipe = new Pipe(topPipeImg);
        topPipe.y = randomPipeY;
        pipes.add(topPipe);

        Pipe bottomPipe = new Pipe(bottomPipeImg);
        bottomPipe.y = topPipe.y + pipeHeight + openingSpace;
        pipes.add(bottomPipe);
    }

    public void draw(Graphics g) {
        // Arka plan
        g.drawImage(backgroundImg, 0, 0, boardWidth, boardHeight, null);

        // Kuş
        g.drawImage(birdImg, birdX, birdY, birdWidth, birdHeight, null);

        // Borular
        for (int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null);
        }

        // Puanı göster
        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        g.drawString("Score: " + score, 10, 30);

        // Oyun bittiğinde mesaj göster
        if (gameOver) {
            g.setColor(Color.red);
            g.setFont(new Font("Arial", Font.BOLD, 32));
            g.drawString("Game Over!", boardWidth / 2 - 80, boardHeight / 2);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if (gameOver) {
            placePipesTimer.stop();
            gameLoop.stop();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            if (gameOver) {
                resetGame();
            } else {
                velocityY = -9;
            }
        }
    }

    public void resetGame() {
        birdY = boardHeight / 2;
        velocityY = 0;
        pipes.clear();
        score = 0; // Puanı sıfırla
        gameOver = false;
        placePipesTimer.start();
        gameLoop.start();
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}

    public static void main(String[] args) {
        JFrame frame = new JFrame("Flappy Bird");
        FlappyJframe flappyJframe = new FlappyJframe();
        frame.add(flappyJframe);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
    }
}