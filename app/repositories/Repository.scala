package repositories

abstract trait Repository[M, I] {
  def getOneById(id: I): M
}
