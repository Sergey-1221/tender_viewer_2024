import * as React from "react"
import {Suspense} from "react";
import DataTableDemo from "./client"


async function Wrapper() {
  let data = await (await fetch("http://localhost:8080/tender", {
    cache: "no-cache"
  })).json();
  return (<DataTableDemo data={data}/>)
}

export default function Page() {
  return (<Suspense fallback={<span>Loading...</span>}>
    <Wrapper/>
  </Suspense>)
  
}
