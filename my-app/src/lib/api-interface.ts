export interface TenderFile {
    name: string;
    url: string;
}

export interface TenderLot {
    name: string,
    info: string
}

export interface Tender {
    id: number;
    name: string;
    vKey: string;
    customer: string;
    price: string;
    currency: string;
    url: string;
    description: string;
    otherInfo: string;
    customerData: string;
    customerContacts: string;
    createdAt: string; 
    closedAt: string;
    files: TenderFile[]; 
    lots: TenderLot[]; 
}
