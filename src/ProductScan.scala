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

  def price: Double = features.get(AttributeKey.price).collect { case na: NumericAttribute => na }.get.value
}

//Conditions will look like
//Condition(AttributeKey.price,case price:NumericAttribute => price.value < 238.9)

//todo can that be Unit?
case class Condition(forKey:AttributeKey,pf:PartialFunction[Attribute,Boolean]) {

  def test(product:Product):Boolean = {
    //if a product doesn't have a feature then false
    product.features.get(forKey).collect{pf}.getOrElse(false)
  }
}

object Condition {
  def create(keyString:String,pf:PartialFunction[Attribute,Boolean]):Condition = {
    Condition(AttributeKey(keyString), pf)
  }
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

  def apply(string:String):Rule = ???

  /**
   *
   * @param rules
   * @param products
   * @return total and average price of all the products that pass the threshold
   */
  def scanProducts(rules:Seq[Rule],products:Iterable[Product],threshold:Double = 50.0):(Double,Double) = {
    val (sum,length) = products.map{ p =>
        p -> rules.map(r => r.findScore(p)).sum  //todo reread the problem - double-check this
    }.filter(_._2 > threshold).
      map(_._1.price). //just need price from here on out
      foldLeft((0.0,0)){(soFar,price) => ((soFar._1 + price),(soFar._2 + 1))} //gather the total and the length in one pass
    (sum,sum/length)
  }
}

/**
 * Extensions to support inlining the rules syntax. Seems OK for a prototype but not for a real system.
 */
extension (sc: StringContext) {
  def rule(args: Any*): Rule = Rule(sc.toString)
}

def main(args:Array[String]):Unit = {
  val handEncodedRule = Rule(100,Seq(
    Condition.create("color",{case ea:EnumeratedAttribute => ea.value == "BLUE"}),
    Condition(AttributeKey.price,{case na:NumericAttribute => na.value < 17.75}),
    Condition.create("quantity",{case na:NumericAttribute => na.value > 750}),
  ))

  val blueCheapBulkThing = Product(new ProductId(1),Map(???))



  val exampleRule = rule"color == BLUE && price < 17.75 && quantity > 750  100"
}




