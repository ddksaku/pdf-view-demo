package com.artifex.mupdfdemo;

/**
 * Created by admin on 3/24/2015.
 */
abstract class LinkInfoVisitor {
    public abstract void visitInternal(LinkInfoInternal li);
    public abstract void visitExternal(LinkInfoExternal li);
    public abstract void visitRemote(LinkInfoRemote li);
}