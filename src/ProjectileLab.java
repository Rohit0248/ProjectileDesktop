import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ProjectileLab extends JFrame {
    static final Color BLUE = new Color(21,101,192), BLUE2 = new Color(13,71,161);
    static final Color BG = new Color(238,243,248), LINE = new Color(216,224,232);
    static final double EARTH=9.81, MOON=1.62, MARS=3.71;
    final SimPanel sim = new SimPanel();
    final JLabel rangeL=new JLabel("0.0 m"), maxL=new JLabel("0.0 m"), timeL=new JLabel("0.00 s"), vxL=new JLabel("0.0 m/s"), vyL=new JLabel("0.0 m/s");
    final JSlider angle=new JSlider(0,85,35), speed=new JSlider(0,50,20), height=new JSlider(0,50,10), target=new JSlider(5,80,30);
    final JLabel angleV=new JLabel(), speedV=new JLabel(), heightV=new JLabel(), targetV=new JLabel();
    final JComboBox<String> object=new JComboBox<>(new String[]{"Cannonball","Pumpkin","Football","Golf ball"});
    final JComboBox<String> gravity=new JComboBox<>(new String[]{"Earth • 9.81 m/s²","Moon • 1.62 m/s²","Mars • 3.71 m/s²"});
    final JCheckBox air=new JCheckBox("Air resistance"), vectors=new JCheckBox("Velocity vectors"), accel=new JCheckBox("Acceleration"), ideal=new JCheckBox("Ideal path",true);
    final JButton fire=new JButton("🚀 FIRE"), pause=new JButton("⏸ Pause"), reset=new JButton("↺ Reset");
    final JLabel mission=new JLabel("Target at 30 m. Predict, then FIRE!"), score=new JLabel("0 pts"), tries=new JLabel("0"), hits=new JLabel("0"), streak=new JLabel("0");
    final JTextArea learnText = new JTextArea();
    String tab="Explore";
    int triesN=0,hitsN=0,scoreN=0,streakN=0;
    Random rnd = new Random();

    public ProjectileLab(){
        super("Projectile Lab — Learn by launching, predicting and testing");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); setMinimumSize(new Dimension(1100,700)); setSize(1250,780); setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        add(topBar(),BorderLayout.NORTH); add(buildContent(),BorderLayout.CENTER);
        wire(); syncControls(); sim.reset();
    }
    JPanel topBar(){
        JPanel p=new JPanel(new BorderLayout()); p.setBackground(BLUE); p.setBorder(BorderFactory.createEmptyBorder(9,14,9,14));
        JLabel title=new JLabel("Projectile Lab"); title.setForeground(Color.WHITE); title.setFont(title.getFont().deriveFont(Font.BOLD,22f));
        JLabel sub=new JLabel("  Learn by launching • predicting • testing"); sub.setForeground(new Color(220,235,255));
        JPanel left=new JPanel(new FlowLayout(FlowLayout.LEFT,2,0)); left.setOpaque(false); left.add(title); left.add(sub);
        JButton demo=new JButton("▶ Demo"), why=new JButton("💡 Why?"); styleTop(demo); styleTop(why);
        demo.addActionListener(e->runDemo("range")); why.addActionListener(e->{selectTab("Learn"); learnText.setText("Why does the path curve?\n\nThe projectile moves forward while gravity pulls it down.\n\nIn simple terms:\n• Sideways motion keeps moving forward.\n• Up-and-down motion changes because of gravity.\n• Put them together and the path becomes a curve.\n\nTry changing one setting at a time and watch what changes.");});
        JPanel right=new JPanel(new FlowLayout(FlowLayout.RIGHT,6,0)); right.setOpaque(false); right.add(demo); right.add(why);
        p.add(left,BorderLayout.WEST); p.add(right,BorderLayout.EAST); return p;
    }
    void styleTop(JButton b){b.setForeground(Color.WHITE); b.setBackground(new Color(255,255,255,40)); b.setBorder(BorderFactory.createLineBorder(new Color(255,255,255,80)));}
    JPanel buildContent(){
        JPanel root=new JPanel(new GridLayout(1,2,10,0)); root.setBorder(BorderFactory.createEmptyBorder(10,10,10,10)); root.setBackground(BG);
        root.add(sim); root.add(side()); return root;
    }
    JPanel side(){
        JPanel s=new JPanel(); s.setBackground(BG); s.setLayout(new BoxLayout(s,BoxLayout.Y_AXIS));
        JPanel tabs=new JPanel(new GridLayout(1,3,5,0)); tabs.setBackground(BG); for(String t:new String[]{"Explore","Challenge","Learn"}){JButton b=new JButton(t); b.addActionListener(e->selectTab(t)); tabs.add(b);} s.add(tabs); s.add(Box.createVerticalStrut(8));
        JPanel explore=card(); explore.add(row(object,gravity));
        explore.add(sliderRow("Angle",angle,angleV, "°",1)); explore.add(sliderRow("Initial speed",speed,speedV," m/s",1)); explore.add(sliderRow("Launch height",height,heightV," m",2)); explore.add(sliderRow("Target distance",target,targetV," m",1));
        JPanel tog=new JPanel(new GridLayout(2,2,5,5)); tog.setOpaque(false); for(JCheckBox c:new JCheckBox[]{air,vectors,accel,ideal}){tog.add(c);} explore.add(tog);
        JPanel buttons=new JPanel(new GridLayout(1,2,6,0)); buttons.setOpaque(false); fire.setBackground(BLUE); fire.setForeground(Color.WHITE); pause.setBackground(new Color(233,240,247)); pause.setForeground(BLUE2); buttons.add(fire);buttons.add(pause); explore.add(buttons); reset.setBackground(new Color(255,235,238)); reset.setForeground(new Color(198,40,40)); explore.add(reset);
        s.add(explore); s.add(Box.createVerticalStrut(8));
        JPanel stats=card(); stats.add(new JLabel("Results")); JPanel sg=new JPanel(new GridLayout(2,3,5,5)); sg.setOpaque(false); statCell(sg,"Range",rangeL);statCell(sg,"Max height",maxL);statCell(sg,"Flight time",timeL);statCell(sg,"Vx",vxL);statCell(sg,"Vy",vyL); stats.add(sg); s.add(stats); s.add(Box.createVerticalStrut(8));
        JPanel chal=card(); chal.setName("challenge"); mission.setFont(mission.getFont().deriveFont(Font.BOLD,12f)); chal.add(mission); JButton nc=new JButton("New challenge"); nc.addActionListener(e->newChallenge()); chal.add(nc); JPanel scoreP=new JPanel(new GridLayout(1,4,5,0)); scoreP.setOpaque(false); scoreP.add(infoCell("Score",score));scoreP.add(infoCell("Tries",tries));scoreP.add(infoCell("Hits",hits));scoreP.add(infoCell("Streak",streak)); chal.add(scoreP); s.add(chal); 
        JPanel learn=card(); learn.setName("learn"); JButton d1=new JButton("45° range demo"), d2=new JButton("x & y demo"), d3=new JButton("air resistance demo"); d1.addActionListener(e->runDemo("range"));d2.addActionListener(e->runDemo("components"));d3.addActionListener(e->runDemo("air")); learn.add(d1);learn.add(d2);learn.add(d3); learnText.setLineWrap(true);learnText.setWrapStyleWord(true);learnText.setEditable(false);learnText.setText("Choose a demo above. Then FIRE and observe what changes.\n\nTeacher tip: change one setting at a time so students can see cause and effect."); learnText.setBackground(new Color(248,250,253)); learn.add(new JScrollPane(learnText)); s.add(learn);
        ((JComponent)s).putClientProperty("x",new Object()); return s;
    }
    JPanel card(){JPanel p=new JPanel(); p.setBackground(Color.WHITE);p.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(LINE),BorderFactory.createEmptyBorder(9,10,9,10)));p.setLayout(new BoxLayout(p,BoxLayout.Y_AXIS)); return p;}
    JPanel row(JComponent a,JComponent b){JPanel p=new JPanel(new GridLayout(1,2,6,0));p.setOpaque(false);p.add(labeled("Projectile",a));p.add(labeled("Gravity",b));return p;}
    JPanel labeled(String name,JComponent c){JPanel p=new JPanel(new BorderLayout(0,3));p.setOpaque(false);JLabel l=new JLabel(name);l.setForeground(new Color(96,112,128));l.setFont(l.getFont().deriveFont(11f));p.add(l,BorderLayout.NORTH);p.add(c,BorderLayout.CENTER);return p;}
    JPanel sliderRow(String n,JSlider sl,JLabel val,String unit,int div){JPanel p=new JPanel(new BorderLayout(8,0));p.setOpaque(false);JPanel a=new JPanel(new BorderLayout());a.setOpaque(false);JLabel l=new JLabel(n);l.setForeground(new Color(96,112,128));l.setFont(l.getFont().deriveFont(11f));a.add(l,BorderLayout.NORTH);a.add(sl,BorderLayout.CENTER);val.setHorizontalAlignment(SwingConstants.CENTER);val.setOpaque(true);val.setBackground(new Color(243,247,251));val.setBorder(BorderFactory.createLineBorder(LINE));p.add(a,BorderLayout.CENTER);p.add(val,BorderLayout.EAST);return p;}
    void statCell(JPanel g,String n,JLabel v){JPanel p=infoCell(n,v);g.add(p);} JPanel infoCell(String n,JLabel v){JPanel p=new JPanel(new BorderLayout());p.setOpaque(true);p.setBackground(new Color(245,248,251));p.setBorder(BorderFactory.createLineBorder(LINE));JLabel l=new JLabel(n,SwingConstants.CENTER);l.setForeground(new Color(96,112,128));l.setFont(l.getFont().deriveFont(10f));v.setHorizontalAlignment(SwingConstants.CENTER);v.setFont(v.getFont().deriveFont(Font.BOLD,13f));p.add(l,BorderLayout.NORTH);p.add(v,BorderLayout.CENTER);return p;}
    void wire(){
        ChangeListener cl=e->{syncControls();sim.reset();sim.repaint();}; angle.addChangeListener(cl); speed.addChangeListener(cl); height.addChangeListener(cl);target.addChangeListener(cl);
        object.addActionListener(e->{sim.reset();}); gravity.addActionListener(e->{sim.reset();}); air.addActionListener(e->{sim.reset();}); vectors.addActionListener(e->sim.repaint()); accel.addActionListener(e->sim.repaint()); ideal.addActionListener(e->sim.repaint());
        fire.addActionListener(e->sim.fire()); pause.addActionListener(e->sim.togglePause()); reset.addActionListener(e->{sim.reset(); triesN=hitsN=scoreN=streakN=0;syncScore();});
        Timer t=new Timer(16,e->{sim.tick(0.016);}); t.start();
    }
    void syncControls(){angleV.setText(angle.getValue()+"°");speedV.setText(speed.getValue()+" m/s");heightV.setText(String.format("%.1f m",height.getValue()/2.0));targetV.setText(target.getValue()+" m");sim.angle=angle.getValue();sim.speed=speed.getValue();sim.height=height.getValue()/2.0;sim.target=target.getValue();sim.g=gravity.getSelectedIndex()==0?EARTH:gravity.getSelectedIndex()==1?MOON:MARS;sim.air=air.isSelected();}
    void updateStats(){SimResult r=sim.predict();rangeL.setText(String.format("%.1f m",r.range));maxL.setText(String.format("%.1f m",r.maxHeight));timeL.setText(String.format("%.2f s",r.time));vxL.setText(String.format("%.1f m/s",r.vx0));vyL.setText(String.format("%.1f m/s",r.vy0));}
    void syncScore(){score.setText(scoreN+" pts");tries.setText(""+triesN);hits.setText(""+hitsN);streak.setText(""+streakN);}
    void selectTab(String t){tab=t; if(t.equals("Learn")) learnText.requestFocus();}
    void runDemo(String t){
        if(t.equals("range")){angle.setValue(45);speed.setValue(20);height.setValue(0);air.setSelected(false);ideal.setSelected(true);learnText.setText("Demo: 45° gives the longest range\n\nWith the same launch and landing height and no air resistance, 45° gives the longest range for a fixed speed. Fire and watch the path.");}
        else if(t.equals("components")){angle.setValue(35);speed.setValue(20);height.setValue(6);air.setSelected(false);vectors.setSelected(true);learnText.setText("Demo: x and y motion are separate\n\nThe projectile keeps moving forward while gravity changes the up-and-down speed. The blue arrow shows velocity.");}
        else {angle.setValue(40);speed.setValue(28);height.setValue(10);air.setSelected(true);learnText.setText("Demo: air resistance\n\nAir resistance pushes against motion. The projectile slows down, so the real path and range are smaller than the ideal path.");}
        sim.reset();
    }
    void newChallenge(){target.setValue(10+rnd.nextInt(56));angle.setValue(15+rnd.nextInt(61));speed.setValue(12+rnd.nextInt(29));height.setValue(rnd.nextInt(21));air.setSelected(rnd.nextDouble()<0.35);mission.setText("Target at "+target.getValue()+" m. Predict, then FIRE!");sim.reset();}

    class SimPanel extends JPanel{
        double angle=35,speed=20,height=5,target=30,g=EARTH; boolean air=false,running=false,paused=false; double x=0,y=5,vx=0,vy=0,t=0; List<Point2D> trail=new ArrayList<>();
        SimPanel(){setPreferredSize(new Dimension(650,600));setBackground(new Color(220,236,251));setBorder(BorderFactory.createLineBorder(new Color(200,217,232)));}
        void reset(){running=false;paused=false;x=0;y=height; t=0;trail.clear();repaint();updateStats();}
        void fire(){if(running)return;running=true;paused=false;t=0;x=0;y=height;double th=Math.toRadians(angle);vx=speed*Math.cos(th);vy=speed*Math.sin(th);trail.clear();triesN++;syncScore();}
        void togglePause(){if(!running)return;paused=!paused;pause.setText(paused?"▶ Resume":"⏸ Pause");}
        void tick(double dt){if(!running||paused)return; int sub=air?4:1; double d=dt/sub; for(int i=0;i<sub;i++){double k=air?0.02:0; double vv=Math.hypot(vx,vy); double ax=-k*vv*vx, ay=-g-k*vv*vy; vx+=ax*d;vy+=ay*d;x+=vx*d;y+=vy*d;t+=d; if(y<0){y=0;finish();break;}}trail.add(new Point2D(x,y)); if(trail.size()>600)trail.remove(0);repaint();}
        void finish(){running=false;double r=x; updateStats(); if(tab.equals("Challenge")){double err=Math.abs(r-target); if(err<1.4){hitsN++;streakN++;scoreN+=100+Math.max(0,(int)Math.round(50-err*30));mission.setText("Hit! Great prediction.");}else{streakN=0;mission.setText(String.format("Missed by %.1f m. Change one setting and try again.",err));}syncScore();}}
        SimResult predict(){double th=Math.toRadians(angle),vx0=speed*Math.cos(th),vy0=speed*Math.sin(th); if(!air){double T=(vy0+Math.sqrt(vy0*vy0+2*g*height))/g; return new SimResult(vx0*T,height+vy0*vy0/(2*g),T,vx0,vy0);} double xx=0,yy=height,vxx=vx0,vyy=vy0,tt=0,max=yy; for(int n=0;n<20000;n++){double dt=.002, vv=Math.hypot(vxx,vyy),k=.02;vxx+=(-k*vv*vxx)*dt;vyy+=(-g-k*vv*vyy)*dt;xx+=vxx*dt;yy+=vyy*dt;tt+=dt;max=Math.max(max,yy);if(yy<=0)break;}return new SimResult(xx,max,tt,vx0,vy0);}
        protected void paintComponent(Graphics gg){super.paintComponent(gg);Graphics2D c=(Graphics2D)gg.create();int w=getWidth(),h=getHeight();c.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);SimResult m=predict();double maxX=Math.max(target+8,m.range+8),maxY=Math.max(18,m.maxHeight+4);int padL=60,padB=48,padT=30,padR=22;double s=Math.min((w-padL-padR)/maxX,(h-padT-padB)/maxY);java.util.function.Function<Double,Integer> X=v->padL+(int)Math.round(v*s);java.util.function.Function<Double,Integer> Y=v->h-padB-(int)Math.round(v*s);
            c.setColor(new Color(191,227,255));c.fillRect(0,0,w,h);c.setColor(new Color(225,235,244));c.setStroke(new BasicStroke(1));for(double gx=0;gx<=maxX;gx+=5)c.drawLine(X.apply(gx),padT,X.apply(gx),h-padB);for(double gy=0;gy<=maxY;gy+=5)c.drawLine(padL,Y.apply(gy),w-padR,Y.apply(gy));c.setColor(new Color(135,166,91));c.fillRect(padL,Y.apply(0.0),w-padL-padR,padB);
            c.setColor(new Color(69,90,100));c.setFont(c.getFont().deriveFont(11f));for(double gx=0;gx<=maxX;gx+=5)c.drawString((int)gx+" m",X.apply(gx)-8,h-padB+20);for(double gy=5;gy<=maxY;gy+=5)c.drawString((int)gy+" m",padL-40,Y.apply(gy)+4);
            int tx=X.apply(target),ty=Y.apply(0.0);c.setColor(new Color(46,125,50));c.setStroke(new BasicStroke(3));c.drawLine(tx,ty-28,tx,ty);c.fillOval(tx-12,ty-42,24,24);c.setColor(Color.WHITE);c.setFont(c.getFont().deriveFont(Font.BOLD,11f));c.drawString("T",tx-4,ty-25);
            if(ideal.isSelected()){c.setColor(new Color(21,101,192,70));c.setStroke(new BasicStroke(2,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND,0,new float[]{7,6},0));Path2DWrap path=new Path2DWrap();double th=Math.toRadians(angle),vx0=speed*Math.cos(th),vy0=speed*Math.sin(th);for(int i=0;i<=120;i++){double tt=m.time*i/120.0,px=vx0*tt,py=height+vy0*tt-.5*g*tt*tt;if(py<0)py=0;path.line(X.apply(px),Y.apply(py),i==0); }c.draw(path.path);}
            if(trail.size()>1){c.setColor(BLUE);c.setStroke(new BasicStroke(3));for(int i=1;i<trail.size();i++){Point2D a=trail.get(i-1),b=trail.get(i);c.drawLine(X.apply(a.x),Y.apply(a.y),X.apply(b.x),Y.apply(b.y));}}
            int cx=X.apply(0.0),cy=Y.apply(height);c.setColor(new Color(55,71,79));c.fillOval(cx-13,cy-7,26,18);c.setColor(new Color(96,125,139));double ang=-Math.toRadians(angle);int bx=(int)(cx+50*Math.cos(ang)),by=(int)(cy+50*Math.sin(ang));c.setStroke(new BasicStroke(12));c.drawLine(cx,cy,bx,by);c.setColor(new Color(55,71,79));c.fillOval(cx-12,cy-2,24,24);
            int px=X.apply(x),py=Y.apply(y);c.setColor(new Color(255,183,77));c.fillOval(px-12,py-12,24,24);c.setColor(new Color(122,76,36));c.fillOval(px-7,py-7,14,14);
            if(vectors.isSelected() && running){drawArrow(c,px,py,px+(int)(vx*s*.18),py-(int)(vy*s*.18),BLUE);} if(accel.isSelected()){drawArrow(c,cx+42,cy-42,cx+42,cy+10,new Color(198,40,40));}
            c.setColor(new Color(23,32,42,190));c.setFont(c.getFont().deriveFont(Font.BOLD,12f));c.drawString("Drag / set controls → aim + speed",12,h-13); c.dispose();
        }
        void drawArrow(Graphics2D c,int x1,int y1,int x2,int y2,Color col){c.setColor(col);c.setStroke(new BasicStroke(2.5f));c.drawLine(x1,y1,x2,y2);double a=Math.atan2(y2-y1,x2-x1);int sz=7;Polygon p=new Polygon();p.addPoint(x2,y2);p.addPoint(x2-(int)(sz*Math.cos(a-.5)),y2-(int)(sz*Math.sin(a-.5)));p.addPoint(x2-(int)(sz*Math.cos(a+.5)),y2-(int)(sz*Math.sin(a+.5)));c.fillPolygon(p);}
    }
    static class SimResult{double range,maxHeight,time,vx0,vy0;SimResult(double r,double h,double t,double x,double y){range=r;maxHeight=h;time=t;vx0=x;vy0=y;}}
    static class Point2D{double x,y;Point2D(double x,double y){this.x=x;this.y=y;}}
    static class Path2DWrap{java.awt.geom.Path2D path=new java.awt.geom.Path2D.Double();void line(int x,int y,boolean first){if(first)path.moveTo(x,y);else path.lineTo(x,y);}}
    public static void main(String[] args){SwingUtilities.invokeLater(()->new ProjectileLab().setVisible(true));}
}
