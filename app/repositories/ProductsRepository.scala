package repositories

import com.datastax.driver.core.{Row, Session}
import models.ProductModel

class ProductsRepository(session: Session)
  extends CassandraRepository[ProductModel, Int](session, "products", "id") {
  override def rowToModel(row: Row): ProductModel = {
    ProductModel(
      row.getInt("id"),
      row.getString("name")
    )
  }
}
