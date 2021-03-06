/*
 * Copyright 2018 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.vatsignupfrontend.helpers.servicemocks

import play.api.http.Status._
import play.api.libs.json.Json
import uk.gov.hmrc.vatsignupfrontend.Constants.{StoreVatNumberKnownFactsMismatchCodeValue, StoreVatNumberNoRelationshipCodeKey, StoreVatNumberNoRelationshipCodeValue}
import uk.gov.hmrc.vatsignupfrontend.helpers.IntegrationTestConstants._
import uk.gov.hmrc.vatsignupfrontend.models.{DateModel, PostCode}

object StoreVatNumberStub extends WireMockMethods {

  def stubStoreVatNumberSuccess(): Unit = {
    when(method = POST, uri = "/vat-sign-up/subscription-request/vat-number", body = Json.obj("vatNumber" -> testVatNumber))
      .thenReturn(status = CREATED)
  }

  def stubStoreVatNumberNoRelationship(): Unit = {
    when(method = POST, uri = "/vat-sign-up/subscription-request/vat-number", body = Json.obj("vatNumber" -> testVatNumber))
      .thenReturn(status = FORBIDDEN, body = Json.obj(StoreVatNumberNoRelationshipCodeKey -> StoreVatNumberNoRelationshipCodeValue))
  }

  def stubStoreVatNumberIneligible(): Unit = {
    when(method = POST, uri = "/vat-sign-up/subscription-request/vat-number", body = Json.obj("vatNumber" -> testVatNumber))
      .thenReturn(status = UNPROCESSABLE_ENTITY)
  }


  def stubStoreVatNumberAlreadySignedUp(): Unit = {
    when(method = POST, uri = "/vat-sign-up/subscription-request/vat-number", body = Json.obj("vatNumber" -> testVatNumber))
      .thenReturn(status = CONFLICT)
  }

  def stubStoreVatNumberFailure(): Unit = {
    when(method = POST, uri = "/vat-sign-up/subscription-request/vat-number", body = Json.obj("vatNumber" -> testVatNumber))
      .thenReturn(status = BAD_REQUEST)
  }


  def stubStoreVatNumberSuccess(postCode: PostCode, registrationDate: DateModel): Unit = {
    when(method = POST, uri = "/vat-sign-up/subscription-request/vat-number", body =
      Json.obj(
        "vatNumber" -> testVatNumber,
        "postCode" -> postCode.postCode,
        "registrationDate" -> registrationDate.toLocalDate.toString
      ))
      .thenReturn(status = CREATED)
  }

  def stubStoreVatNumberKnownFactsMismatch(postCode: PostCode, registrationDate: DateModel): Unit = {
    when(method = POST, uri = "/vat-sign-up/subscription-request/vat-number", body =
      Json.obj(
        "vatNumber" -> testVatNumber,
        "postCode" -> postCode.postCode,
        "registrationDate" -> registrationDate.toLocalDate.toString
      ))
      .thenReturn(status = FORBIDDEN, body = Json.obj(StoreVatNumberNoRelationshipCodeKey -> StoreVatNumberKnownFactsMismatchCodeValue))
  }

  def stubStoreVatNumberInvalid(postCode: PostCode, registrationDate: DateModel): Unit = {
    when(method = POST, uri = "/vat-sign-up/subscription-request/vat-number", body =
      Json.obj(
        "vatNumber" -> testVatNumber,
        "postCode" -> postCode.postCode,
        "registrationDate" -> registrationDate.toLocalDate.toString
      ))
      .thenReturn(status = PRECONDITION_FAILED)
  }

  def stubStoreVatNumberIneligible(postCode: PostCode, registrationDate: DateModel): Unit = {
    when(method = POST, uri = "/vat-sign-up/subscription-request/vat-number", body =
      Json.obj(
        "vatNumber" -> testVatNumber,
        "postCode" -> postCode.postCode,
        "registrationDate" -> registrationDate.toLocalDate.toString
      ))
      .thenReturn(status = UNPROCESSABLE_ENTITY)
  }

  def stubStoreVatNumberAlreadySignedUp(postCode: PostCode, registrationDate: DateModel): Unit = {
    when(method = POST, uri = "/vat-sign-up/subscription-request/vat-number", body =
      Json.obj(
        "vatNumber" -> testVatNumber,
        "postCode" -> postCode.postCode,
        "registrationDate" -> registrationDate.toLocalDate.toString
      ))
      .thenReturn(status = CONFLICT)
  }

}