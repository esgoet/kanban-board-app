import {ReactNode} from "react";
import {FetchBaseQueryError} from "@reduxjs/toolkit/query";
import {SerializedError} from "@reduxjs/toolkit";

interface StatusHandlerProps {
    isLoading: boolean;
    isError: boolean;
    isSuccess: boolean;
    error: FetchBaseQueryError | SerializedError | undefined;
    children: ReactNode
}
export default function StatusHandler({isLoading, isError, isSuccess, error, children}: StatusHandlerProps) {

    if (isLoading) {
        return <div>Loading...</div>;
    }

    if (isError) {
        if (error) {
            if ('status' in error) {
                const errorMessage = 'error' in error ? error.error :  JSON.stringify(error.data);

                return  <div>Error {error.status}: {errorMessage !== "null" ? errorMessage : "Unknown Error"}</div>;
            }
            console.log("test")
            return <div>Error: {error.message}</div>;
        }

    }

    if (isSuccess) {
        return (
            <>
                {children}
            </>
        )
    }
};