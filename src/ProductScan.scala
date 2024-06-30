import scala.collection.immutable.*

class ProductId(val v:Int) extends AnyVal

class AttributeKey(val id:String) extends AnyVal

object AttributeKey {
  val name = AttributeKey("name")
  val price = AttributeKey("price")
}

case class Attribute[T](id:AttributeKey,v:T)

case class Product(id:ProductId,features:Map[AttributeKey,Attribute[?]]) {
  def name = features(AttributeKey.name)
  def price = features(AttributeKey.price)
}

//todo do I really need this case class?
case class Condition[T](c:PartialFunction[T,Boolean])

case class Rule(score:Int,conditions:Seq[Condition[?]])

def main(args:Array[String]):Unit = ???




