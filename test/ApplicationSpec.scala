import java.io.File
import models.ProductModel
import play.api
import play.api.{Mode, Environment, ApplicationLoader}
import play.api.ApplicationLoader.Context
import play.api.test._
import play.api.test.Helpers._
import org.scalatestplus.play._
import repositories.Repository

class MockProductsRepository extends Repository[ProductModel, Int] {
  override def getOneById(id: Int): ProductModel = {
    ProductModel(1, "Mocked Chair")
  }
}

class FakeApplicationComponents(context: Context) extends AppComponents(context) {
  override lazy val productsRepository = new MockProductsRepository()
}

class FakeAppLoader extends ApplicationLoader {
  override def load(context: Context): api.Application =
    new FakeApplicationComponents(context).application
}

class ApplicationSpec extends PlaySpec with OneAppPerSuite {

  override implicit lazy val app: api.Application = {
    val appLoader = new FakeAppLoader
    appLoader.load(
      ApplicationLoader.createContext(
        new Environment(
          new File("."), ApplicationLoader.getClass.getClassLoader, Mode.Test)
      )
    )
  }

  "Application" should {

    "send 404 on a bad request" in {
      val Some(wrongRoute) = route(FakeRequest(GET, "/boum"))

      status(wrongRoute) mustBe NOT_FOUND
    }

    "render the index page and tell us about the first product" in {
      val Some(home) = route(FakeRequest(GET, "/"))

      status(home) mustBe OK
      contentType(home) mustBe Some("text/html")
      contentAsString(home) must include ("Your new application is ready. The name of product #1 is Mocked Chair.")
    }
  }

}
