import scala.collection.immutable.*

def main(args:Array[String]):Unit = {

  val handEncodedRule = Rule(100,Seq(
    Condition(aid"color",{case ea:EnumeratedAttribute => ea.value == "BLUE"}),
    Condition(AttributeKey.price,{case na:NumericAttribute => na.value < 17.75}),
    Condition(aid"quantity",{case na:NumericAttribute => na.value > 750}),
  ))

  val rules = Seq(handEncodedRule)

  //perfect match
  val blueCheapBulkThing = Product(new ProductId(1),Seq(
    EnumeratedAttribute(aid"name","Cheap Blue Bulk Thing"),
    NumericAttribute(aid"price",4.95),
    EnumeratedAttribute(aid"color","BLUE"),
    NumericAttribute(aid"quantity",2000),
    EnumeratedAttribute(aid"size","M"),
    BooleanAttribute(aid"GitD",false)
  ))

  //good enough
  val greenCheapBulkThing = Product(new ProductId(1), Seq(
    EnumeratedAttribute(aid"name", "Cheap Green Bulk Thing"),
    NumericAttribute(aid"price", 4.95),
    EnumeratedAttribute(aid"color", "GREEN"),
    NumericAttribute(aid"quantity", 2000),
    EnumeratedAttribute(aid"size", "L"),
    BooleanAttribute(aid"GitD", true)
  ))

  //does not match
  val redExpensiveRareThing = Product(new ProductId(1), Seq(
    EnumeratedAttribute(aid"name", "Red Expensive Bulk Thing"),
    NumericAttribute(aid"price", 945.3),
    EnumeratedAttribute(aid"color", "RED"),
    NumericAttribute(aid"quantity", 3),
    EnumeratedAttribute(aid"size", "M"),
  ))

  val products = Seq(blueCheapBulkThing,greenCheapBulkThing,redExpensiveRareThing)
  val (total,average) = Rule.scanProducts(rules,products,50.0)
  println(s"total: $total, average: $average")
}

/**
 * Extensions to support quick construction from strings.
 */
extension (sc: StringContext) {
  /**
   * exampleRule = rule"color == BLUE && price < 17.75 && quantity > 750  100"
   */
  def rule(args: Any*): Either[String,Rule] = {
    Left("I'm putting my energy into explaining why this is a bad path to go down instead of demonstrating how tidy the brittle code could be")
  }

  def aid(args: Any*): AttributeKey = new AttributeKey(sc.parts.head)
}

case class Rule(fullScore:Double,conditions:Seq[Condition]) {
  def findScore(product:Product):Double = {
    val total = conditions.map { condition =>
      condition.test(product)
    }.filter(_ == true).size
    (total * fullScore) / conditions.size
  }
}

object Rule {

  /**
   * @return total and average price of all the products that pass the threshold
   */
  def scanProducts(rules:Seq[Rule],products:Iterable[Product],threshold:Double = 50.0):(Double,Double) = {
    val (sum,length) = products.map{ p =>
        p -> rules.map(r => r.findScore(p)).sum
      }.filter(_._2 > threshold).
      map(_._1.price). //just need price from here on out
      foldLeft((0.0,0)){(soFar,price) => ((soFar._1 + price),(soFar._2 + 1))} //gather the total and the length in one pass, ready for streaming
    (sum,sum/length)
  }
}


//A condition will look like
//Condition(AttributeKey.price,case price:NumericAttribute => price.value < 238.9)
case class Condition(forKey:AttributeKey,pf:PartialFunction[Attribute,Boolean]) {

  def test(product:Product):Boolean = {
    //if a product doesn't have an attribute of the right id or type then false
    product.attributes.get(forKey).collect{pf}.getOrElse(false)
  }
}

case class Product(id:ProductId, attributes:Map[AttributeKey,Attribute]) {
  //todo check that every product has a name and price
  def name = attributes(AttributeKey.name)

  def price: Double = attributes.get(AttributeKey.price).collect { case na: NumericAttribute => na }.get.value
}

object Product {
  def apply(id:ProductId, attributes:Seq[Attribute]):Product = {
    new Product(id,attributes.map(f => f.id -> f).toMap)
  }
}

class ProductId(val v:Int) extends AnyVal

trait Attribute {
  def id:AttributeKey
  type T
  def value:T
}

//these subclasses of Attribute are not going to be type-specific enough to save a mess, but it's really easy to make new ones that are. It'd be not terrible to write a parser of the example rule to build these.
case class NumericAttribute(id:AttributeKey,value:Double) extends Attribute{
  type T = Double
}

case class BooleanAttribute(id:AttributeKey,value:Boolean) extends Attribute{
  type T = Boolean
}

//Strings for now. Type safe enumerations are really easy, and you don't want them all mixed together anyway. BLUE and XXLARGE shouldn't be mixed.
case class EnumeratedAttribute(id:AttributeKey,value:String) extends Attribute{
  type T = String
}

class AttributeKey(val id:String) extends AnyVal {
  override def toString: String = s"AttributeKey($id)"
}

object AttributeKey {
  val name = AttributeKey("name")
  val price = AttributeKey("price")
}