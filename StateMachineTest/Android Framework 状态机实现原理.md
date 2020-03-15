# Android Framework 状态机实现原理

## 前言

状态模式是比较一种常见的设计模式，为了解决某个事物很多状态之间的切换很多的业务逻辑，这样可以避免很多if-else嵌套，不仅提高了代码的可读性，同样拓展性也得到很大的提升。在Android系统中，像wifi、蓝牙的源码中，看到状态机的使用，所以来学习下。状态机是对状态模式的一种拓展，使用了Handler机制来对消息的分发，同时还增加了 state-hierarchy 的模式，状态同样也有父状态，如果当前状态没有能力去处理当前消息，会去让父状态来处理。下面会从使用的角度来分析源码，来深入理解Android Framework 状态的机制。

我们使用Android源码中给的例子：

```java
   
//首先初始化状态机，然后调用start()方法
public static Hsm1 makeHsm1() {
        System.out.println("makeHsm1 E");
        Hsm1 sm = new Hsm1("hsm1");  //Hsm1 extend StateMachine
        sm.start();
        System.out.println("makeHsm1 X");
        return sm;
}

//状态机的构造方法
   Hsm1(String name) {
        super(name);
        System.out.println("ctor E");

        //添加状态，其中mP1是mS1和mS2的父状态，
        addState(mP1);
        addState(mS1, mP1);
        addState(mS2, mP1);
        addState(mP2);

        // 需要设置一个初始状态
        setInitialState(mS1);
        System.out.println("ctor X");
    }

    //调用状态机方法，让状态机运行起来，然后发消息
    Hsm1 hsm1 = Hsm1.makeHsm1();
    hsm1.sendMessage(hsm1.obtainMessage(Hsm1.CMD_1));
    hsm1.sendMessage(hsm1.obtainMessage(Hsm1.CMD_2));
```

## 状态机初始化

状态机的初始化是调用了StateMachine.start()方法，看下源码：

```java
public void start() {
    // 确保handler创建了
    SmHandler smh = mSmHandler;
    if (smh == null) return;

    /** Send the complete construction message */
    smh.completeConstruction();
}

private final void completeConstruction() {
    if (mDbg) mSm.log("completeConstruction: E");

    //计算每种状态的最大深度，maxDepth就是所有状态中，父状态最多的的路径长度
    int maxDepth = 0;
    for (StateInfo si : mStateInfo.values()) {
        int depth = 0;
        for (StateInfo i = si; i != null; depth++) {
            i = i.parentStateInfo;
        }
        if (maxDepth < depth) {
            maxDepth = depth;
        }
    }
    if (mDbg) mSm.log("completeConstruction: maxDepth=" + maxDepth);

    //创建状态栈
    mStateStack = new StateInfo[maxDepth];
    mTempStateStack = new StateInfo[maxDepth];
    //填充栈
    setupInitialStateStack();

    //发送init指令，其实是触发了初始状态的执行 
    sendMessageAtFrontOfQueue(obtainMessage(SM_INIT_CMD, mSmHandlerObj));

    if (mDbg) mSm.log("completeConstruction: X");
}
```

这里为什么要创建两个状态栈呢？因为例如一个状态有一个父状态，当切换到这个状态的时候肯定是父状态先执行enter，然后在子状态执行enter，退出这个状态的时候，肯定要先子状态执行exit，然后父状态再执行exit，但是栈堆的机制就是后进先出，所以只能用两个栈堆来确保enter和exit的执行顺序。下面来看看 上面代码的 setupInitialStateStack()。

```java
private final void setupInitialStateStack() {
    if (mDbg) {
        mSm.log("setupInitialStateStack: E mInitialState=" + mInitialState.getName());
    }

    StateInfo curStateInfo = mStateInfo.get(mInitialState);
    for (mTempStateStackCount = 0; curStateInfo != null; mTempStateStackCount++) {
        mTempStateStack[mTempStateStackCount] = curStateInfo;
        curStateInfo = curStateInfo.parentStateInfo;
    }

    // Empty the StateStack
    mStateStackTopIndex = -1;

    moveTempStateStackToStateStack();
}

private final int moveTempStateStackToStateStack() {
    int startingIndex = mStateStackTopIndex + 1;
    int i = mTempStateStackCount - 1;
    int j = startingIndex;
    while (i >= 0) {
        if (mDbg) mSm.log("moveTempStackToStateStack: i=" + i + ",j=" + j);
        mStateStack[j] = mTempStateStack[i];
        j += 1;
        i -= 1;
    }

    mStateStackTopIndex = j - 1;
    if (mDbg) {
        mSm.log("moveTempStackToStateStack: X mStateStackTop=" + mStateStackTopIndex
                + ",startingIndex=" + startingIndex + ",Top="
                + mStateStack[mStateStackTopIndex].state.getName());
    }
    return startingIndex;
}

```

上面的代码执行逻辑解释了我之前所说，所以mStateStack和mTempStateStack里面的顺序完全是相反的，这么做的目的是为了确保父状态和子状态 enter和exit方法执行顺序的正确性，这也是他们当初设计的想法吧。

## 状态机处理消息

在Handler的处理机制，我们很容易得到消息的处理都是在handleMessage()方法中执行的，下面一起看下状态机对这个方法的重写吧？

```java
@Override
public final void handleMessage(Message msg) {
    if (!mHasQuit) {
        if (mDbg) mSm.log("handleMessage: E msg.what=" + msg.what);

        /** Save the current message */
        mMsg = msg;

        /** State that processed the message */
        State msgProcessedState = null;
        if (mIsConstructionCompleted) {
            //2.正常的逻辑
            msgProcessedState = processMsg(msg);
            //1.还记得状态机初始化过程中最后的一步吧 => sendMessageAtFrontOfQueue
        } else if (!mIsConstructionCompleted && (mMsg.what == SM_INIT_CMD)
                   && (mMsg.obj == mSmHandlerObj)) {
            /** Initial one time path. */
            mIsConstructionCompleted = true;
            invokeEnterMethods(0);
        } else {
            throw new RuntimeException("StateMachine.handleMessage: "
                                       + "The start method not called, received msg: " + msg);
        }
        //3.切换状态
        performTransitions(msgProcessedState, msg);

        // We need to check if mSm == null here as we could be quitting.
        if (mDbg && mSm != null) mSm.log("handleMessage: X");
    }
}
```

首先来看看第一步，当状态机接收到了SM_INIT_CMD消息的时候，做了哪些事情：

```java
private final void invokeEnterMethods(int stateStackEnteringIndex) {
    for (int i = stateStackEnteringIndex; i <= mStateStackTopIndex; i++) {
        if (mDbg) mSm.log("invokeEnterMethods: " + mStateStack[i].state.getName());
        mStateStack[i].state.enter();
        mStateStack[i].active = true;
    }
}
```

很简单，就是执行栈中所有状态的enter方法，且置为激活状态，哪个先执行，显然是父状态吧？再来看看第二步，这个是正常执行的逻辑：

```java
private final State processMsg(Message msg) {
    StateInfo curStateInfo = mStateStack[mStateStackTopIndex];
    if (mDbg) {
        mSm.log("processMsg: " + curStateInfo.state.getName());
    }

    if (isQuit(msg)) {
        //如果状态是退出状态，则切换至 mQuittingState状态，先不看
        transitionTo(mQuittingState);
    } else {
        //执行状态类的processMessage方法，如果当前状态类无法处理会返回false
        //交于父状态处理，如果所有的父状态都无法处理则调用 状态机的unhandleMessage方法
        while (!curStateInfo.state.processMessage(msg)) {
            /**
                     * Not processed
                     */
            curStateInfo = curStateInfo.parentStateInfo;
            if (curStateInfo == null) {
                /**
                         * No parents left so it's not handled
                         */
                mSm.unhandledMessage(msg);
                break;
            }
            if (mDbg) {
                mSm.log("processMsg: " + curStateInfo.state.getName());
            }
        }
    }
    return (curStateInfo != null) ? curStateInfo.state : null;
}
```

每个状态类都是继承于IState接口，其processMessage都是由开发者复写，返回false就是当前状态类无法处理，true代表已经处理了。最后unhandleMessage，状态机也只是打印了一下而已：

```java
protected void unhandledMessage(Message msg) {
    if (mSmHandler.mDbg) loge(" - unhandledMessage: msg.what=" + msg.what);
}
```

接着，我们再继续分析第三步（切换状态）， performTransitions(msgProcessedState, msg) :

```java
private void performTransitions(State msgProcessedState, Message msg) {
    
    //原始状态
    State orgState = mStateStack[mStateStackTopIndex].state;

    //记录日志
    //...

    //目的状态，由transitionTo方法切换状态
    State destState = mDestState;
    if (destState != null) {
        //执行状态切换中，enter和exit的方法执行
        while (true) {
            if (mDbg) mSm.log("handleMessage: new destination call exit/enter");

            //利用stack和 temp stack 完成状态切换过程中enter和exit方法的执行，
            //这里不准备详细讲了，后面用图来分析，有兴趣可以自行分析
            StateInfo commonStateInfo = setupTempStateStackWithStatesToEnter(destState);
            invokeExitMethods(commonStateInfo);
            int stateStackEnteringIndex = moveTempStateStackToStateStack();
            invokeEnterMethods(stateStackEnteringIndex);

            //在完成状态切换过程中，原先队列中的消息会被移到队列的最前面，优先执行
            moveDeferredMessageAtFrontOfQueue();

            if (destState != mDestState) {
                // A new mDestState so continue looping
                destState = mDestState;
            } else {
                // No change in mDestState so we're done
                break;
            }
        }
        mDestState = null;
    }
    //...
}

```

![](F:\project\github\External-Study\StateMachineTest\switch_state.jpg)

可以看到如果从C 状态切换到E状态，首先我们要做的事情就是想C状态以及C状态的父状态先执行exit方法，执行顺序是C->B->A，但是A也是E的祖宗状态，从C切换至E，不能执行A的exit状态，所以上面很多的代码做的就是这个事情，所以由C切换至E的代码逻辑就是 C.exit -> B.exit -> D.enter->E.exit。

