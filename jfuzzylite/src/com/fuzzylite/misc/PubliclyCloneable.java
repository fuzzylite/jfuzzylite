/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fuzzylite.misc;

/**
 *
 * @author jcrada
 */
public interface PubliclyCloneable extends Cloneable {

    public Object clone() throws CloneNotSupportedException;
}
