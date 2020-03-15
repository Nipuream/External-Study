package com.example.statemachinetest.impl;

import android.os.Message;
import com.example.statemachinetest.utils.State;
import com.example.statemachinetest.utils.StateMachine;


/**
 *
 *
 * Hsm1 hsm1 = Hsm1.makeHsm1();
 * hsm1.sendMessage(hsm1.obtainMessage(Hsm1.CMD_1));
 * hsm1.sendMessage(hsm1.obtainMessage(Hsm1.CMD_2));
 *
 * I/System.out: makeHsm1 E
 * I/System.out: ctor E
 * I/System.out: ctor X
 * I/System.out: makeHsm1 X
 * I/System.out: mP1.enter
 * I/System.out: mS1.enter
 * I/System.out: S1.processMessage what=1
 * I/System.out: mS1.exit
 * I/System.out: mS1.enter
 * I/System.out: S1.processMessage what=2
 * I/System.out: mP1.processMessage what=2
 * I/System.out: mS1.exit
 * I/System.out: mS2.enter
 * I/System.out: mS2.processMessage what=2
 * I/System.out: mS2.processMessage what=3
 * I/System.out: mS2.exit
 * I/System.out: mP1.exit
 * I/System.out: mP2.enter
 * I/System.out: P2.processMessage what=3
 * I/System.out: P2.processMessage what=4
 * I/System.out: P2.processMessage what=5
 * I/System.out: mP2.exit
 * I/System.out: halting
 */
public class Hsm1 extends StateMachine {

    public static final int CMD_1 = 1;
    public static final int CMD_2 = 2;
    public static final int CMD_3 = 3;
    public static final int CMD_4 = 4;
    public static final int CMD_5 = 5;

    P1 mP1 = new P1();
    S1 mS1 = new S1();
    S2 mS2 = new S2();
    P2 mP2 = new P2();

    public static Hsm1 makeHsm1() {
        System.out.println("makeHsm1 E");
        Hsm1 sm = new Hsm1("hsm1");
        sm.start();
        System.out.println("makeHsm1 X");
        return sm;
    }


    Hsm1(String name) {
        super(name);
        System.out.println("ctor E");

        // Add states, use indentation to show hierarchy
        addState(mP1);
        addState(mS1, mP1);
        addState(mS2, mP1);
        addState(mP2);

        // Set the initial state
        setInitialState(mS1);
        System.out.println("ctor X");
    }

    @Override
    protected void onHalting() {
        super.onHalting();

        System.out.println("halting");
        synchronized (this){
            this.notifyAll();
        }
    }

    class P1 extends State {

        @Override
        public void enter() {
            super.enter();
            System.out.println("mP1.enter");
        }

        @Override
        public boolean processMessage(Message msg) {
            boolean retVal;
            System.out.println("mP1.processMessage what=" + msg.what);
            switch(msg.what) {
                case CMD_2:
                    // CMD_2 will arrive in mS2 before CMD_3
                    sendMessage(obtainMessage(CMD_3));
                    deferMessage(msg);
                    transitionTo(mS2);
                    retVal = HANDLED;
                    break;
                default:
                    // Any message we don't understand in this state invokes unhandledMessage
                    retVal = NOT_HANDLED;
                    break;
            }
            return retVal;
        }

        @Override
        public void exit() {
            super.exit();
            System.out.println("mP1.exit");
        }
    }

    class S1 extends State {

        @Override
        public void enter() {
            super.enter();
            System.out.println("mS1.enter");
        }

        @Override
        public boolean processMessage(Message msg) {
            System.out.println("S1.processMessage what=" + msg.what);
            if (msg.what == CMD_1) {
                // Transition to ourself to show that enter/exit is called
                transitionTo(mS1);
                return HANDLED;
            } else {
                // Let parent process all other messages
                return NOT_HANDLED;
            }
        }

        @Override
        public void exit() {
            super.exit();
            System.out.println("mS1.exit");
        }
    }

    class S2 extends State {

        @Override
        public void enter() {
            super.enter();
            System.out.println("mS2.enter");
        }

        @Override
        public boolean processMessage(Message msg) {
            boolean retVal;
            System.out.println("mS2.processMessage what=" + msg.what);
            switch(msg.what) {
                case(CMD_2):
                    sendMessage(obtainMessage(CMD_4));
                    retVal = HANDLED;
                    break;
                case(CMD_3):
                    deferMessage(msg);
                    transitionTo(mP2);
                    retVal = HANDLED;
                    break;
                default:
                    retVal = NOT_HANDLED;
                    break;
            }
            return retVal;
        }

        @Override
        public void exit() {
            super.exit();
            System.out.println("mS2.exit");
        }
    }

    class P2 extends State {

        @Override
        public void enter() {
            super.enter();
            System.out.println("mP2.enter");
            sendMessage(obtainMessage(CMD_5));
        }

        @Override
        public boolean processMessage(Message msg) {
            System.out.println("P2.processMessage what=" + msg.what);
            switch(msg.what) {
                case(CMD_3):
                    break;
                case(CMD_4):
                    break;
                case(CMD_5):
                    transitionToHaltingState();
                    break;
            }
            return HANDLED;
        }

        @Override
        public void exit() {
            super.exit();
            System.out.println("mP2.exit");
        }
    }
}
