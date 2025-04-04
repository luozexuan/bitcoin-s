package org.bitcoins.core.util

import org.bitcoins.crypto.DoubleSha256Digest

import scala.annotation.tailrec

sealed trait BinaryTreeDoubleSha256Digest {
  def value: Option[DoubleSha256Digest] =
    this match {
      case n: NodeDoubleSha256Digest   => Some(n.v)
      case l: LeafDoubleSha256Digest   => Some(l.v)
      case EmptyTreeDoubleSha256Digest => None
    }

  def left: Option[BinaryTreeDoubleSha256Digest] =
    this match {
      case n: NodeDoubleSha256Digest   => Some(n.l)
      case _: LeafDoubleSha256Digest   => None
      case EmptyTreeDoubleSha256Digest => None
    }

  def right: Option[BinaryTreeDoubleSha256Digest] =
    this match {
      case n: NodeDoubleSha256Digest   => Some(n.r)
      case _: LeafDoubleSha256Digest   => None
      case EmptyTreeDoubleSha256Digest => None
    }

  /** A function to find the first occurrence of a predicate inside a
    * [[org.bitcoins.core.util.BinaryTree BinaryTree]].
    */
  def findFirstDFS(
      t: DoubleSha256Digest): Option[BinaryTreeDoubleSha256Digest] = {
    val f = { (x: DoubleSha256Digest) => x == t }
    @tailrec
    def loop(
        subTree: BinaryTreeDoubleSha256Digest,
        remainder: List[BinaryTreeDoubleSha256Digest])
        : Option[BinaryTreeDoubleSha256Digest] = {
      subTree match {
        case EmptyTreeDoubleSha256Digest =>
          if (remainder.isEmpty) None else loop(remainder.head, remainder.tail)
        case LeafDoubleSha256Digest(x) =>
          if (f(x)) Some(LeafDoubleSha256Digest(x))
          else if (remainder.isEmpty) None
          else loop(remainder.head, remainder.tail)
        case NodeDoubleSha256Digest(v, l, r) =>
          if (f(v)) Some(NodeDoubleSha256Digest(v, l, r))
          else loop(l, r :: remainder)
      }
    }
    loop(this, List.empty)
  }

  /** Checks if the [[org.bitcoins.core.util.BinaryTree BinaryTree]] contains a
    * certain element.
    */
  def contains(t: DoubleSha256Digest): Boolean =
    findFirstDFS(t).isDefined

  /** Inserts an element into one of the two branches in a
    * [[org.bitcoins.core.util.BinaryTree BinaryTree]]. If it cannot insert it
    * because the branches are not empty, it throws a
    * [[scala.RuntimeException RuntimeException]].
    */
  def insert(t: DoubleSha256Digest): BinaryTreeDoubleSha256Digest = {
    insert(LeafDoubleSha256Digest(t))
  }

  /** Inserts a tree into one of the two branches in a
    * [[org.bitcoins.core.util.BinaryTree BinaryTree]] If it cannot insert it
    * because the branches are not empty, it throws a
    * [[scala.RuntimeException RuntimeException]].
    */
  def insert(
      subTree: BinaryTreeDoubleSha256Digest): BinaryTreeDoubleSha256Digest =
    this match {
      case n: NodeDoubleSha256Digest =>
        if (n.l == EmptyTreeDoubleSha256Digest)
          NodeDoubleSha256Digest(n.v, subTree, n.r)
        else if (n.r == EmptyTreeDoubleSha256Digest)
          NodeDoubleSha256Digest(n.v, n.l, subTree)
        else
          throw new RuntimeException(
            "There was no empty branch to insert the new t: " + subTree + "inside of tree: " + this)
      case l: LeafDoubleSha256Digest =>
        NodeDoubleSha256Digest(l.v, subTree, EmptyTreeDoubleSha256Digest)
      case EmptyTreeDoubleSha256Digest => subTree
    }

  /** Removes the subTree from the parentTree. */
  def remove(
      subTree: BinaryTreeDoubleSha256Digest): BinaryTreeDoubleSha256Digest = {
    // TODO: Optimize into a tail recursive function
    this match {
      case EmptyTreeDoubleSha256Digest =>
        EmptyTreeDoubleSha256Digest
      case l: LeafDoubleSha256Digest =>
        if (l == subTree) EmptyTreeDoubleSha256Digest
        else l
      case n: NodeDoubleSha256Digest =>
        if (n == subTree) EmptyTreeDoubleSha256Digest
        else
          NodeDoubleSha256Digest(n.v, remove(subTree), remove(subTree))
    }
  }

  /** Replaces all instances of the original tree with the replacement tree. */
  def replace(
      originalTree: BinaryTreeDoubleSha256Digest,
      replacementTree: BinaryTreeDoubleSha256Digest)
      : BinaryTreeDoubleSha256Digest = {
    // TODO: Optimize this into a tail recursive function
    this match {
      case EmptyTreeDoubleSha256Digest =>
        if (originalTree == EmptyTreeDoubleSha256Digest) replacementTree
        else EmptyTreeDoubleSha256Digest
      case l: LeafDoubleSha256Digest =>
        if (l == originalTree) replacementTree else l
      case n: NodeDoubleSha256Digest =>
        if (n == originalTree) replacementTree
        else
          NodeDoubleSha256Digest(n.v,
                                 replace(originalTree, replacementTree),
                                 replace(originalTree, replacementTree))
    }
  }
}

case class NodeDoubleSha256Digest(
    v: DoubleSha256Digest,
    l: BinaryTreeDoubleSha256Digest,
    r: BinaryTreeDoubleSha256Digest)
    extends BinaryTreeDoubleSha256Digest

case class LeafDoubleSha256Digest(v: DoubleSha256Digest)
    extends BinaryTreeDoubleSha256Digest

case object EmptyTreeDoubleSha256Digest extends BinaryTreeDoubleSha256Digest
