package com.ecommerce.models

/** Structure des quatre jeux de donnees (Q2.1).
  * Les champs suivent exactement la partie DESCRIPTION DES DONNEES du sujet.
  * Chaque ligne lue devient un objet de ce type, plus facile a utiliser.
  */
final case class Transaction(
    transaction_id: String,
    user_id: String,
    product_id: String,
    merchant_id: String,
    amount: Double,
    timestamp: String,
    location: String,
    payment_method: String,
    category: String
)

final case class User(
    user_id: String,
    age: Int,
    annual_income: Double,
    city: String,
    customer_segment: String,
    preferred_categories: Seq[String],
    registration_date: String
)

final case class Product(
    product_id: String,
    name: String,
    category: String,
    price: Double,
    merchant_id: String,
    rating: Double,
    stock: Int
)

final case class Merchant(
    merchant_id: String,
    name: String,
    category: String,
    region: String,
    commission_rate: Double,
    establishment_date: String
)

/** Une ligne du rapport de qualite des donnees (Q2.4).
  * Les trois derniers compteurs viennent du bonus Q2.5 (identifiants orphelins).
  */
final case class QualityReportRow(
    dataset: String,
    nb_lignes_lues: Long,
    nb_lignes_valides: Long,
    nb_lignes_rejetees: Long,
    taux_rejet: Double,
    nb_valeurs_nulles: Long,
    nb_user_id_orphelins: Long = 0,
    nb_product_id_orphelins: Long = 0,
    nb_merchant_id_orphelins: Long = 0
)