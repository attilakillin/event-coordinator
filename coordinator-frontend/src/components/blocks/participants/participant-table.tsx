import Button from "@/components/builtin/button";
import Id from "@/components/types/id";
import { Participant } from "@/components/types/participant";
import { useEffect } from "react";
import { Column, TableInstance, TableState, UsePaginationInstanceProps, UsePaginationState, usePagination, useTable } from "react-table";


interface ComponentProps {
    data: (Participant & Id)[],
    action?: {
        name: string,
        callback: (id: string) => void
    }
};

const table_columns = [
    {
        Header: 'Vezetéknév', accessor: 'lastName'
    },
    {
        Header: 'Keresztnév', accessor: 'firstName'
    },
    {
        Header: 'Email cím', accessor: 'email'
    },
    {
        Header: 'Telefonszám', accessor: 'phoneNumber'
    },
    {
        Header: 'Lakcím', accessor: 'address'
    },
    {
        Header: 'Megjegyzések', accessor: 'notes'
    }
] as Column<Participant & Id>[];

export default function ParticipantTable(props: ComponentProps) {
    const table = useTable(
        { columns: table_columns, data: props.data },
        usePagination
    ) as TableInstance<Participant & Id>
        & UsePaginationInstanceProps<Participant & Id>;

    const {
        getTableProps,
        getTableBodyProps,
        headerGroups,
        prepareRow,
        page,

        pageOptions,
        state,

        canPreviousPage,
        canNextPage,
        gotoPage,
        nextPage,
        previousPage,

        setPageSize
    } = table;
    const { pageIndex } = state as TableState<Participant> & UsePaginationState<Participant>;

    useEffect(() => setPageSize(10), [setPageSize]);

    return <>
        {/* eslint-disable react/jsx-key */}
        <div className='overflow-x-auto'>
            <table className='w-full border-collapse text-theme-800' {...getTableProps()}>
                <thead>
                    {
                        headerGroups.map(group => (
                            <tr {...group.getHeaderGroupProps()}>
                                {
                                    group.headers.map(col => (
                                        <th className='px-4 py-3 bg-theme-100 text-lg font-normal'
                                            {...col.getHeaderProps()}
                                        >
                                            {col.render('Header')}
                                        </th>
                                    ))
                                }
                                {props.action && <th className='bg-theme-100'></th>}
                            </tr>
                        ))
                    }
                </thead>
                <tbody {...getTableBodyProps()}>
                    {
                        page.map(row => {
                            prepareRow(row);
                            return (
                                <tr className='border-b border-theme-600 hover:bg-theme-100'
                                    {...row.getRowProps()}
                                >
                                    {
                                        row.cells.map(cell => {
                                            return (
                                                <td className='p-4' {...cell.getCellProps()}>
                                                    {cell.render('Cell')}
                                                </td>
                                            )
                                        })
                                    }
                                    {props.action && <td>
                                        <Button
                                            onClick={() => props.action!.callback(row.original.id.toString())}
                                        >
                                            {props.action!.name}
                                        </Button>
                                    </td>}
                                </tr>
                            )
                        })
                    }
                </tbody>
            </table>
        </div>

        <div className='mt-4 flex flex-row justify-center'>
            <Button onClick={() => previousPage()} disabled={!canPreviousPage}>Előző</Button>
            <div className='mx-4 grid place-items-center'>
                {pageIndex + 1} / {pageOptions.length}
            </div>
            <Button onClick={() => nextPage()} disabled={!canNextPage}>Következő</Button>
        </div>
    </>;
}
