package com.gensler.scalavro.tests

import scala.reflect.runtime.universe._

import com.gensler.scalavro.util.Union
import com.gensler.scalavro.util.Union.{union, prove}

class UnionSpec extends AvroSpec {

  "The union type helpers" should "allow one to define unions" in {

    type ISB = union [Int] #or [String] #or [Boolean] #apply

    def unionFunction[T : prove [ISB] #containsType](t: T) {}

    unionFunction(55)
    unionFunction("hello")
    unionFunction(false)
    // unionFunction(math.Pi) // fails to compile
  }

  it should "wrap union type definitions in a 'friendly' class" in {
    val wrapped = new Union[union [Int] #or [String] #apply]

    wrapped.contains[Int] should be (true)
    wrapped.contains[String] should be (true)
    wrapped.contains[Double] should be (false)

    wrapped assign 55
    wrapped.value[Int] should be (Some(55))

    wrapped assign "hi, union!"
    wrapped.value[String] should be (Some("hi, union!"))

    wrapped.value[Int] should be (None)

    def unionFunction[T : wrapped.containsType] {}
    unionFunction[Int]
    unionFunction[String]
  }

  it should "handle unary unions, no matter how silly that seems" in {
    val unary = new Union[union [Int] #apply]
    unary.contains[Int] should be (true)
    unary.contains[Boolean] should be (false)
  }

}