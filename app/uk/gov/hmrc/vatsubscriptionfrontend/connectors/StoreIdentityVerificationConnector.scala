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

package uk.gov.hmrc.vatsubscriptionfrontend.connectors

import javax.inject.Inject

import play.api.libs.json.Json
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.http.HttpClient
import uk.gov.hmrc.vatsubscriptionfrontend.config.AppConfig
import uk.gov.hmrc.vatsubscriptionfrontend.httpparsers.StoreIdentityVerificationHttpParser._
import uk.gov.hmrc.play.http.logging.MdcLoggingExecutionContext._
import uk.gov.hmrc.vatsubscriptionfrontend.Constants.StoreIdentityVerificationUrlKey

import scala.concurrent.Future

class StoreIdentityVerificationConnector @Inject()(http: HttpClient,
                                                   applicationConfig: AppConfig) {
  def storeIdentityVerification(vatNumber: String,
                                continueUrl: String
                               )(implicit hc: HeaderCarrier): Future[StoreIdentityVerificationResponse] =
    http.POST(
      url = applicationConfig.storeIdentityVerificationUrl(vatNumber),
      body = Json.obj(StoreIdentityVerificationUrlKey -> continueUrl)
    )
}