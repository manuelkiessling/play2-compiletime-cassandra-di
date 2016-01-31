package repositories

trait Repository[M, I] {
  def getOneById(id: I): M
}
