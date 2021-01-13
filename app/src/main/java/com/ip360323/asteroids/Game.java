package com.ip360323.asteroids;

import android.content.Context;
import android.content.res.Resources;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import com.ip360323.asteroids.Entities.Asteroid;
import com.ip360323.asteroids.Entities.Bullet;
import com.ip360323.asteroids.Entities.GLEntity;
import com.ip360323.asteroids.Entities.Player;
import com.ip360323.asteroids.Entities.PlayerLifeEntity;
import com.ip360323.asteroids.Entities.Star;
import com.ip360323.asteroids.Entities.Text;

import java.util.ArrayList;
import java.util.Random;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class Game extends GLSurfaceView implements GLSurfaceView.Renderer{

    private Shader shader;
    private Shader textShader;
    private static final float BG_COLOR[] = {0/255f, 0/255f, 0/255f, 1f}; //RGBA
    private final ArrayList<Text> _texts = new ArrayList<>();

    private static final int BULLET_COUNT = (int)(Bullet.TIME_TO_LIVE/ Player.TIME_BETWEEN_SHOTS)+1;
    private final Bullet[] _bullets = new Bullet[BULLET_COUNT];

    private Player player;
    private static final int EGL_VERSION = 2;
    private static final int STAR_COUNT = 50;
    private static final int ASTROID_COUNT = 6;
    private final ArrayList<Star> _stars = new ArrayList<>();
    private final ArrayList<Asteroid> _asteroids = new ArrayList<>();
    private final ArrayList<Asteroid> asteroidsToAdd = new ArrayList<>();

    private static final String fpsString = "FPS: %d";
    private static final String levelString ="%d";
    private static final String scoreString ="%d";
    private Text fpsText;
    private Text levelText;
    private Text scoreText;

    private ParticleSystem particleSystem;
    private int particleTexture;
    private int particleTextureOrange;

    private static final int SMALL_ASTEROID_SCORE = 100;
    private static final int MEDIUM_ASTEROID_SCORE = 50;
    private static final int LARGE_ASTEROID_SCORE = 25;
    private static final float LEVEL_SCORE_MULTIPLIER = 0.25f;
    private int score;

    private final ArrayList<PlayerLifeEntity> playerLives = new ArrayList<>();

    private static final long SECOND_IN_NANOSECONDS = 1000000000;
    private static final float NANOSECONDS_TO_SECONDS = 1.0f / SECOND_IN_NANOSECONDS;

    public static float WORLD_WIDTH = 160f; //all dimensions are in meters
    public static float WORLD_HEIGHT = 90f;
    private static final float METERS_TO_SHOW_X = 160f; //160m x 90m, the entire game world in view
    private static final float METERS_TO_SHOW_Y = 0f; //TODO: calculate to match screen aspect ratio
    private static final float QUARTER_WORLD_HEIGHT = (WORLD_HEIGHT * 0.25f);
    private int screenWidth;
    private int screenHeight;

    private boolean readyToStart = false;
    private boolean started = false;


    private int level = 0;
    private static final float NEW_ASTEROIDS_PER_LEVEL = 3;

    private Viewport viewport;

    @Override
    public void onSurfaceCreated(final GL10 unused, final EGLConfig config) {
        GLManager.buildProgram(getContext()); //compile, link and upload our GL program
        GLES20.glClearColor(BG_COLOR[0], BG_COLOR[1], BG_COLOR[2], BG_COLOR[3]); //set clear color
        GLES20.glEnable(GLES20.GL_DEPTH_TEST); // enable depth
        Resources res = getResources();

        shader = new Shader();
        shader.LoadShader(res, R.raw.basicvertex, R.raw.basicfragment);

        textShader = new Shader();
        textShader.LoadShader(res, R.raw.textvert, R.raw.textfrag);

        particleTexture = TextureManager.getInstance().GetTextureHandle(res, R.drawable.particle);
        particleTextureOrange = TextureManager.getInstance().GetTextureHandle(res, R.drawable.particleorange);
        particleSystem = new ParticleSystem(res);
    }

    private void Start()
    {
        level = 0;
        score = 0;
        _asteroids.clear();
        asteroidsToAdd.clear();
        _texts.clear();
        _stars.clear();
        particleSystem.Clear();

        final float fpsTextX = 2;
        final float fpsTextY = WORLD_HEIGHT - 6;
        fpsText = new Text(textShader, fpsString, fpsTextX, fpsTextY);
        final float levelTextX = WORLD_WIDTH / 2;
        final int levelTextY = 4;
        levelText = new Text(textShader, String.format(levelString, 0), levelTextX, levelTextY);
        final float scoreTextX = WORLD_WIDTH - WORLD_WIDTH / 4;
        final int scoreTextY = 4;
        scoreText = new Text(textShader, String.format(scoreString, 0), scoreTextX, scoreTextY);

        Resources res = getResources();
        player = new Player(shader, res, particleSystem, WORLD_WIDTH/2f, WORLD_HEIGHT/2f);
        for(int i = 0; i < player.lifes-1; i++)
        {
            playerLives.add(new PlayerLifeEntity(shader, res, 2 + i * 2, 4));
        }

        for(int i = 0; i < BULLET_COUNT; i++) {
            _bullets[i] = new Bullet(shader, res);
        }

        Random r = new Random();
        for(int i = 0; i < STAR_COUNT; i++){
            _stars.add(new Star(shader, res, r.nextInt((int)WORLD_WIDTH), r.nextInt((int)WORLD_HEIGHT)));
        }
        SpawnAsteroids();
    }

    private void NextLevel()
    {
        level++;
        levelText.setString(String.format(levelString, level));
        SpawnAsteroids();
    }

    private void SpawnAsteroids()
    {
        int spawnCount = (int) Math.ceil((ASTROID_COUNT + level * NEW_ASTEROIDS_PER_LEVEL) * 0.5f);
        for(int i = 0; i < spawnCount; i++)
        {
            for(int y = 0; y < 2;  y++)
            {
                final float offset = QUARTER_WORLD_HEIGHT * 0.25f;
                _asteroids.add(new Asteroid(shader, getResources(),
                        (WORLD_WIDTH/(float)spawnCount) * i,
                        offset + y * (WORLD_HEIGHT - (offset * 2)),
                        Asteroid.Type.LARGE));
            }
        }
    }

    public Game(Context context) {
        super(context);
        Init();
    }
    public Game(Context context, AttributeSet attrs) {
        super(context, attrs);
        Init();
    }

    private void Init()
    {
        setEGLContextClientVersion(EGL_VERSION);
        setPreserveEGLContextOnPause(true);
        setRenderer(this);
    }

    @Override
    public void onSurfaceChanged(final GL10 unused, final int width, final int height) {
        screenWidth = width;
        screenHeight = height;
        viewport = new Viewport(screenWidth, screenHeight, METERS_TO_SHOW_X, METERS_TO_SHOW_Y);
        WORLD_WIDTH =  viewport.metersToShowX;
        WORLD_HEIGHT = viewport.metersToShowY;
        readyToStart = true;

        GLES20.glViewport(0, 0, width, height);
    }



    @Override
    public void onDrawFrame(final GL10 unused) {
        update();
        render();
    }

    private void AddScore(int score)
    {
        this.score += score;
        scoreText.setString(String.format(scoreString, this.score));
    }


    //trying a fixed time-step with accumulator, courtesy of
//   https://gafferongames.com/post/fix_your_timestep/
    private final double dt = 0.016666;
    private double accumulator = 0.0;
    private double currentTime = System.nanoTime()*NANOSECONDS_TO_SECONDS;
    private int fps = 0;
    private int fpsCounter = 0;
    private float fpsAccu = 0;
    private static final float fpsTimer = 1.0f;
    private void update(){

        if(!started)
        {
            if(readyToStart)
            {
                Start();
                started = true;
            }
        }
        else
        {
            final double newTime = System.nanoTime()*NANOSECONDS_TO_SECONDS;
            final double frameTime = newTime - currentTime;
            currentTime = newTime;
            accumulator += frameTime;

            while(accumulator >= dt){
                if(_asteroids.size() == 0)
                {
                    NextLevel();
                }

                for(final Asteroid a : _asteroids){
                    a.update(dt);
                }
                for(final Bullet b : _bullets){
                    if(b.isDead()){ continue; } //skip
                    b.update(dt);
                }

                ParseEntityEvent(player.update(dt), player);
                collisionDetection();
                removeDeadEntities();
                particleSystem.Update((float)dt);
                accumulator -= dt;
                Input.getInstance().Update(); // resets button state after every frame
            }

            fpsCounter++;
            fpsAccu += frameTime;
            if(fpsAccu > fpsTimer)
            {
                fps = fpsCounter;
                fpsCounter = 0;
                fpsAccu = 0;
            }
            fpsText.setString(String.format(fpsString, fps));
        }
    }

    private void ParseEntityEvent(GLEntity.EntityEvent e, Player p)
    {
        if(e.wantsToShoot)
        {
            for(final Bullet b : _bullets) {
                if(b.isDead()) {
                    p.Shoot();
                    b.fireFrom(p);
                    break;
                }
            }
            e.wantsToShoot = false;
        }
    }

    private void removeDeadEntities(){

        if(player.lifes != playerLives.size()+1)
        {
            if(playerLives.size() > 0)
            {
                playerLives.remove(playerLives.size()-1);
            }
        }

        if(player.isDead())
        {
            Start();
        }

        Asteroid temp;
        final int count = _asteroids.size();
        for(int i = count-1; i >= 0; i--){
            temp = _asteroids.get(i);
            if(temp.isDead()){

                if(temp.asteroidType == Asteroid.Type.LARGE)
                {
                    asteroidsToAdd.add(new Asteroid(shader, getResources(), temp.centerX(), temp.centerY(), Asteroid.Type.MEDIUM));
                    asteroidsToAdd.add(new Asteroid(shader, getResources(), temp.centerX(), temp.centerY(), Asteroid.Type.MEDIUM));
                }
                else if(temp.asteroidType == Asteroid.Type.MEDIUM)
                {
                    asteroidsToAdd.add(new Asteroid(shader, getResources(), temp.centerX(), temp.centerY(), Asteroid.Type.SMALL));
                    asteroidsToAdd.add(new Asteroid(shader, getResources(), temp.centerX(), temp.centerY(), Asteroid.Type.SMALL));
                }
                _asteroids.remove(i);
            }
        }

        _asteroids.addAll(asteroidsToAdd);
        asteroidsToAdd.clear();
    }

    private void collisionDetection(){
        for(final Bullet b : _bullets) {
            if(b.isDead()){ continue; } //skip dead bullets
            for(final Asteroid a : _asteroids) {
                if(b.isColliding(a)){
                    if(a.isDead()){continue;}
                    //TODO: This should be managed somewhere else?
                    if(a.asteroidType == Asteroid.Type.SMALL)
                    {
                        AddScore((int)(SMALL_ASTEROID_SCORE + (SMALL_ASTEROID_SCORE * (LEVEL_SCORE_MULTIPLIER * level))));
                        ParticleExplosion(a.centerX(), a.centerY(), 0, 25, particleTexture);
                    }
                    else if(a.asteroidType == Asteroid.Type.MEDIUM)
                    {
                        AddScore((int)(MEDIUM_ASTEROID_SCORE + (MEDIUM_ASTEROID_SCORE * (LEVEL_SCORE_MULTIPLIER * level))));
                        ParticleExplosion(a.centerX(), a.centerY(), 0, 15, particleTexture);
                    }
                    else
                    {
                        AddScore((int)(LARGE_ASTEROID_SCORE + (LARGE_ASTEROID_SCORE * (LEVEL_SCORE_MULTIPLIER * level))));
                        ParticleExplosion(a.centerX(), a.centerY(), 0, 10, particleTexture);
                    }
                    b.onCollision(a); //notify each entity so they can decide what to do
                    a.onCollision(b);
                }
            }
        }
        for(final Asteroid a : _asteroids) {
            if(a.isDead()){continue;}
            if(player.isColliding(a)){
                if(!player.respawning)
                {
                    player.onCollision(a);
                    a.onCollision(player);
                    ParticleExplosion(player.centerX(), player.centerY(), 0, 30, particleTextureOrange);
                }
            }
        }
    }

    private void ParticleExplosion(float x, float y, float z, int count, int texture)
    {
        for(int i = 0; i < count; i++)
        {
            final float minSpeed = 2.0f;
            final float maxSpeed = 7.0f;
            final float minSize = 0.05f;
            final float maxSize = 0.3f;
            final float minTime = 0.7f;
            final float maxTime = 1.5f;
            particleSystem.SpawnParticle(
                    x,
                    y,
                    z,
                    Utils.RandomAngle(),
                    Utils.between(minSpeed, maxSpeed),
                    Utils.between(minSize, maxSize),
                    Utils.between(minTime, maxTime),
                    texture);
        }
    }


    private void render(){
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT); //clear buffer to background color
        //setup a projection matrix by passing in the range of the game world that will be mapped by OpenGL to the screen.

        float[] projectionMatrix = viewport.GetProjectionMatrix();

        for(final Bullet b : _bullets) {
            if (b.isDead()) {
                continue;
            } //skip
            b.render(projectionMatrix);
        }

        for(final Asteroid a : _asteroids){
            a.render(projectionMatrix);
        }
        for(final Star s : _stars){
            s.render(projectionMatrix);
        }
        player.render(projectionMatrix);
        for(final Text t : _texts){
            t.render(projectionMatrix);
        }

        fpsText.render(projectionMatrix);
        scoreText.render(projectionMatrix);
        levelText.render(projectionMatrix);

        for(final PlayerLifeEntity life : playerLives)
        {
            life.render(projectionMatrix);
        }
        particleSystem.Render(projectionMatrix);
    }
}
