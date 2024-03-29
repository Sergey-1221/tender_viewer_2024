import Image from "next/image";

import Menu from "./menu";
import DataTableDemo from "./table_1";
import Tender_view from "./tender";


export default function Home() {
  return (
      <main className="flex flex-col items-center justify-between p-24">
        <Menu />
        <DataTableDemo />
    </main>
  );
}
