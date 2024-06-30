import scala.collection.immutable.*

class ProductId(val v:Int) extends AnyVal

class AttributeKey(val id:String) extends AnyVal

object AttributeKey {
  val name = AttributeKey("name")
  val price = AttributeKey("price")
}

trait Attribute(id:AttributeKey) {
  type T
  def value:T
}

//these subclasses of Attribute are not going to be type-specific enough to save a mess, but it's really easy to make new ones that are. It'd be not terrible to write a parser of the example rule to build these.
case class NumericAttribute(id:AttributeKey,value:Double) extends Attribute(id){
  type T = Double
}

case class BooleanAttribute(id:AttributeKey,value:Boolean) extends Attribute(id){
  type T = Boolean
}

//Strings for now. Type safe enumerations are really easy, and you don't want them all mixed together anyway. BLUE and XXLARGE shouldn't be mixed.
case class EnumeratedAttribute(id:AttributeKey,value:String) extends Attribute(id){
  type T = String
}

case class Product(id:ProductId,features:Map[AttributeKey,Attribute]) {
  def name = features(AttributeKey.name)
  def price = features(AttributeKey.price)
}

//Conditions will look like
//Condition(AttributeKey.price,case price:NumericAttribute => price.value < 238.9)

//todo can that be Unit?
case class Condition[T](forKey:AttributeKey,pf:PartialFunction[Attribute,Boolean]) {

  def test(product:Product):Boolean = {
    //if a product doesn't have a feature then false
    product.features.get(forKey).collect{pf}.getOrElse(false)
  }
}

//I'll likely want to specialize Score once I learn more about it, at least what values are in-bounds. Maybe it can be an Int.
class Score(v:Double) extends AnyVal

case class Rule(fullScore:Double,conditions:Seq[Condition[?]]) {
  def findScore(product:Product):Score = {
    val total = conditions.map { condition =>
      condition.test(product)
    }.filter(_ == true).size
    new Score((total * fullScore) / conditions.size)
  }
}

object Rule {
  /**
   *
   * @param rules
   * @param products
   * @return total and average price of all the products that pass the threshold
   */
  def scanProducts(threshold:Score,rules:Seq[Rule],products:Iterable[Product]):(Double,Double) = ???
}

def main(args:Array[String]):Unit = {
  val colorKey = AttributeKey("color")
  val quantityKey = AttributeKey("quantity")



}




