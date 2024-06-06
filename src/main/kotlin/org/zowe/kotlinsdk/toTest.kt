////
//// This program and the accompanying materials are made available under the terms of the
//// Eclipse Public License v2.0 which accompanies this distribution, and is available at
//// https://www.eclipse.org/legal/epl-v20.html
////
//// SPDX-License-Identifier: EPL-2.0
////
//// Copyright IBA Group 2020
////
//
//package org.zowe.kotlinsdk
//
//import org.zowe.kotlinsdk.core.datasets.DatasetsAPI
//import org.zowe.kotlinsdk.impl.zosmf.*
//import org.zowe.kotlinsdk.impl.zosmf.datasets.data.*
//
//fun main() {
//  println("Start here")
//
//  val ktorHttpClient = getUnsafeKTorHttpClient()
//
//  val connection = UserPassConnection("172.20.2.121", "10443", "https", "ZOSMFAD", "ZOSMF")
//  val requestCanceller = RequestCanceller()
//  val zosmfApiProvider = ZosmfDefaultAPIProvider(ktorHttpClient, connection, requestCanceller)
//
//  val listDsRequest = ZosmfListDatasetsRequest(
//    mask = "ZOSMFAD.*",
//    attributes = XIBMAttributes(isTotal = true),
//    maxItems = 5
//  )
//
//  val getDsInfoRequest = ZosmfGetDatasetInfoRequest(
//    dsName = "ZOSMFAD.ARST.BBBBB"
//  )
//
//  val listDsMembersRequest = ZosmfListDatasetMembersRequest(
//    datasetName = "ZOSMFAD.REXX"
//  )
//
//  val retrieveDsContentRequest = ZosmfRetrieveDatasetContentRequest(
//    dsName = "ZOSMFAD.REXX",
//    memberName = "SAMPLE"
//  )
//
//  val datasetsApi = zosmfApiProvider.getApi(DatasetsAPI::class.java)
//
////  val x = coroutineScope {
////    val result = async {
////      val resultListDs = datasetsApi.listDatasets(listDsRequest)
////    }
////    async {
////      delay(1000L)
////      requestCanceller.cancelCurrentRequest()
////    }
////    result
////  }
////
////  runBlocking {
////    val res = x
////    println()
////  }
//
////  val resultListDs = datasetsApi.listDatasets(listDsRequest)
////  val resultGetDsInfo = datasetsApi.getDatasetInfo(getDsInfoRequest)
////  val resultListDsMembers = datasetsApi.listDatasetMembers(listDsMembersRequest)
////  val resultRetrieveDsContentRequest = datasetsApi.retrieveDatasetContent(retrieveDsContentRequest)
//
//  println("Ktor finished")
//
//  ktorHttpClient.close()
//
//  println("End here")
//}