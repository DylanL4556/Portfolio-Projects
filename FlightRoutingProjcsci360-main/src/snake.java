import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

public class snake {


    public class GameFrame extends JFrame {

        public GameFrame() {
            this.add(new GamePanel());
            this.setTitle("Snake Game");
            this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            this.setResizable(false);
            this.pack();
            this.setVisible(true);
            this.setLocationRelativeTo(null);
        }


    }


    public class Point {

        public int x;
        public int y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }


    public static class GamePanel extends JPanel implements ActionListener {

        static final int SCREEN_WIDTH = 600;
        static final int SCREEN_HEIGHT = 600;
        static final int UNIT_SIZE = 25;
        static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / UNIT_SIZE;
        static final int DELAY = 75;
        final int[] x = new int[GAME_UNITS];
        final int[] y = new int[GAME_UNITS];
        int bodyParts = 6;
        int applesEaten;
        int appleX;
        int appleY;
        char direction = 'R';
        boolean running = false;
        Timer timer;
        Random random;

        GamePanel() {
            random = new Random();
            this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
            this.setBackground(Color.black);
            this.setFocusable(true);
            this.addKeyListener(new MyKeyAdapter());
            startGame();


        }

        public void startGame() {
            newApple();
            running = true;
            timer = new Timer(DELAY, this);
            timer.start();
            this.requestFocusInWindow();
        }

        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            draw(g);
        }

        public void draw(Graphics g) {
            if (running) {
                /*
                 * Draw the apple
                 */
                g.setColor(Color.RED);
                g.fillOval(appleX * UNIT_SIZE, appleY * UNIT_SIZE, UNIT_SIZE, UNIT_SIZE);

                /*
                 * Draw the snake
                 */
                for (int i = 0; i < bodyParts; i++) {
                    if (i == 0) {
                        g.setColor(Color.GREEN); // Draw head
                        g.fillRect(x[i] * UNIT_SIZE, y[i] * UNIT_SIZE, UNIT_SIZE, UNIT_SIZE);
                    } else {
                        g.setColor(new Color(45, 180, 0)); // Draw body
                        g.fillRect(x[i] * UNIT_SIZE, y[i] * UNIT_SIZE, UNIT_SIZE, UNIT_SIZE);
                    }
                }

                /*
                 * Draw the current score
                 */
                g.setColor(Color.RED);
                g.setFont(new Font("Ink Free", Font.BOLD, 40));
                FontMetrics metrics = getFontMetrics(g.getFont());
                g.drawString("Score: " + applesEaten, (SCREEN_WIDTH - metrics.stringWidth("Score: " + applesEaten)) / 2, g.getFont().getSize());
            } else {
                gameOver(g);
            }
        }

        public void newApple() {
            appleX = random.nextInt((int)(SCREEN_WIDTH/UNIT_SIZE));
            appleY = random.nextInt((int)(SCREEN_HEIGHT/UNIT_SIZE));

            for(int i = bodyParts;i>0;i--) {
                if((x[i] == appleX) && (y[i] == appleY)) {
                    newApple();
                }
            }
        }

        public void move() {
            // Shift the body of the snake
            for (int i = bodyParts; i > 0; i--) {
                x[i] = x[(i - 1)];
                y[i] = y[(i - 1)];
            }

            // Update the head of the snake based on the current direction
            switch (direction) {
                case 'U' -> y[0] = y[0] - 1;
                case 'D' -> y[0] = y[0] + 1;
                case 'L' -> x[0] = x[0] - 1;
                case 'R' -> x[0] = x[0] + 1;
            }
        }

        public void checkApple() {
            if ((x[0] == appleX) && (y[0] == appleY)) {
                bodyParts++;
                applesEaten++;
                newApple();

            }
        }

        public void checkCollisions() {
            // Checks if the head collides with body
            for (int i = bodyParts; i > 0; i--) {
                if ((x[0] == x[i]) && (y[0] == y[i])) {
                    running = false;
                }
            }

            // Checks if the head touches the left border
            if (x[0] < 0) {
                running = false;
            }

            // Checks if the head touches the right border
            if (x[0] > SCREEN_WIDTH / UNIT_SIZE) {
                running = false;
            }

            // Checks if the head touches the top border
            if (y[0] < 0) {
                running = false;
            }

            // Checks if the head touches the bottom border
            if (y[0] > SCREEN_HEIGHT / UNIT_SIZE) {
                running = false;
            }

            // Stop the timer
            if (!running) {
                timer.stop();
            }
        }

        public void gameOver(Graphics g) {
            // Set the color and font of the text
            g.setColor(Color.RED);
            g.setFont(new Font("Ink Free", Font.BOLD, 75));

            // Calculate the position of the text
            FontMetrics metrics1 = getFontMetrics(g.getFont());
            int x = (SCREEN_WIDTH - metrics1.stringWidth("Game Over")) / 2;
            int y = SCREEN_HEIGHT / 2;

            // Draw the game over text
            g.drawString("Game Over", x, y);

            // Set the color and font of the score
            g.setColor(Color.WHITE);
            g.setFont(new Font("Ink Free", Font.BOLD, 30));

            // Calculate the position of the score
            FontMetrics metrics2 = getFontMetrics(g.getFont());
            x = (SCREEN_WIDTH - metrics2.stringWidth("Score: " + applesEaten)) / 2;
            y = g.getFont().getSize();

            // Draw the score
            g.drawString("Score: " + applesEaten, x, y);

        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (running) {
                move();
                checkApple();
                checkCollisions();
            }
            repaint();
        }

        public class MyKeyAdapter extends KeyAdapter {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_A:
                        if (direction != 'R') {
                            direction = 'L';
                        }
                        break;
                    case KeyEvent.VK_D:
                        if (direction != 'L') {
                            direction = 'R';
                        }
                        break;
                    case KeyEvent.VK_W:
                        if (direction != 'D') {
                            direction = 'U';
                        }
                        break;
                    case KeyEvent.VK_S:
                        if (direction != 'U') {
                            direction = 'D';
                        }
                        break;
                }
            }
        }
    }


    public void main(String[] args) {
        new GameFrame();
    }

}
