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

package uk.gov.hmrc.vatsignupfrontend

object Constants {
  val StoreVatNumberNoRelationshipCodeKey = "CODE"
  val StoreVatNumberNoRelationshipCodeValue = "RELATIONSHIP_NOT_FOUND"
  val StoreVatNumberKnownFactsMismatchCodeValue = "KNOWN_FACTS_MISMATCH"
  val StoreIdentityVerificationUrlKey = "journeyLink"

  val GetCompanyNameCodeKey = "company_name"

  object Enrolments {
    val agentEnrolmentKey = "HMRC-AS-AGENT"
    val VatDecEnrolmentKey = "HMCE-VATDEC-ORG"
    val VatReferenceKey = "VATRegNo"
    val IRSAEnrolmentKey = "IR-SA"
    val IRSAReferenceKey = "UTR"
  }

  object TaxEnrolments {
    val serviceName = "HMRC-MTD-VAT"
  }

  val skipIvJourneyValue = "skipIdentityVerification"
}
