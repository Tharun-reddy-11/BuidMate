import {createContext,useContext,useState,type ReactNode} from 'react';
import type {AuthUser} from './types';
type AuthValue={user:AuthUser|null;save:(u:AuthUser)=>void;logout:()=>void};
const AuthContext=createContext<AuthValue>({user:null,save:()=>{},logout:()=>{}});
export function AuthProvider({children}:{children:ReactNode}){const [user,setUser]=useState<AuthUser|null>(()=>{try{return JSON.parse(localStorage.getItem('buildmate_auth')||'null')}catch{return null}});const save=(u:AuthUser)=>{localStorage.setItem('buildmate_auth',JSON.stringify(u));setUser(u)};const logout=()=>{localStorage.removeItem('buildmate_auth');setUser(null)};return <AuthContext.Provider value={{user,save,logout}}>{children}</AuthContext.Provider>}
export const useAuth=()=>useContext(AuthContext);

