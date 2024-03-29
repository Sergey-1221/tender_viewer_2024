import {Suspense} from "react";
import {Table, TableBody, TableHeader, TableRow, TableCell} from '@/components/ui/table';
import {Tender} from "@/lib/api-interface";

async function Content({id}: { id: number }) {
	let res = await fetch(`http://localhost:8080/tender/${id}`, {
		method: "GET",
		cache: "no-cache"
	})
	let data: Tender = await res.json<Tender>();
	console.log(data);
	return <div className="z-10 max-w-5xl w-full items-center justify-between font-mono text-sm">
		<h1 className="text-2xl font-bold tracking-tight text-gray-900 sm:text-3xl"> 

			{data.name}
		</h1> 

		<a href="https://icetrade.by/tenders/all/view/1124291"><b>Перейти</b></a><br/>
		<br/>
		<b>Заказчик</b> <span>{data.customer}</span> - {data.customerData}; {data.customerContacts}<br/>
		<b>Цена</b> <span>{data.price} {data.currency}</span><br/>
		<b>Время создания и закрытия</b> {new Date(data.createdAt).toDateString()} - {data.closedAt == null ? "Invalid date" : new Date(data.closedAt).toDateString()}
		<br/>
		<a href={data.url}><b className={"text-blue-700"}>Ссылка на источник</b></a>
		<div className={"flex flex-row"}>
			<b>Описание</b>
			<span className={"mx-4"}>
				{data.description}
			</span>
		</div>
		<div className={"flex flex-row"}>
			<b>Дополнительная информация</b>
			<span className={"mx-4"}>
				{data.otherInfo || '-'}
			</span>
		</div>
		<b>Файлы</b>
		<ul className={"m-4"}>
			{(data.files ?? []).map((f => {return <li key={f.name}><a href={f.url}>{f.name}</a></li>}))}
		</ul>
		<b>
			Лоты
		</b>
		<ul>
			{(data.lots ?? []).map((l) => {return <><hr className={"border-black"}/><li key={l.name}>{l.name} - {l.info}</li></>})}
		</ul>
	</div>
}


export default function Page({params, searchParams}: {
  params: { slug: string };
  searchParams?: { [key: string]: string | string[] | undefined }
}) {
	let id = searchParams.id;
	return (
		<div className="container">
			<Suspense fallback={<span>Loading...</span>}>
				<Content id={+id}/>
				
			</Suspense>
		</div>

	)
}
